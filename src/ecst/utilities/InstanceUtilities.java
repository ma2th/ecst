package ecst.utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.InstanceComparator;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ecst.combiner.SubjectIDList;

/**
 * This class contains various method for instances handling.
 * 
 * @author Matthias Ring
 * 
 */
public class InstanceUtilities {

	/**
	 * Evaluates the given classifier with a leave-subject-out cross-validation.
	 * 
	 * @param classifier
	 * @param instances
	 * @param subjectIDs
	 * @param subjectsPerHoldOut
	 * @return
	 * @throws Exception
	 */
	public static Evaluation evaluateLeaveSubjectOut(Classifier classifier, Instances instances, SubjectIDList subjectIDs, int subjectsPerHoldOut)
			throws Exception {
		int numberOfFolds;
		Evaluation evaluation = null;
		Set<Integer> uniqueIDs = null;
		List<Integer> testIndices = null;
		Iterator<Integer> iterator = null;

		evaluation = new Evaluation(instances);
		uniqueIDs = subjectIDs.getUniqueIDs();
		numberOfFolds = uniqueIDs.size() / subjectsPerHoldOut;

		iterator = uniqueIDs.iterator();
		for (int fold = 0; fold < numberOfFolds; fold++) {
			evaluateFold(fold, classifier, instances, getTestInstanceIndices(subjectIDs, subjectsPerHoldOut, iterator), evaluation);
		}

		// if uniqueIDs.size() % subjectsPerHoldOut != 0
		if (iterator.hasNext()) {
			testIndices = new LinkedList<Integer>();
			while (iterator.hasNext()) {
				testIndices.addAll(subjectIDs.getIndicesForID(iterator.next()));
			}
			Collections.sort(testIndices);
			evaluateFold(numberOfFolds, classifier, instances, testIndices, evaluation);
		}

		return evaluation;
	}

	/**
	 * Internal method for leave-subject-out cross-validation. Performs one fold
	 * of the cross-validation.
	 * 
	 * @param fold
	 * @param classifier
	 * @param instances
	 * @param testInstanceIndices
	 * @param evaluation
	 * @throws Exception
	 */
	private static void evaluateFold(int fold, Classifier classifier, Instances instances, List<Integer> testInstanceIndices, Evaluation evaluation)
			throws Exception {
		Instances test = new Instances(instances);
		Instances training = new Instances(instances);

		prepareTrainingAndTestSet(instances, training, test, testInstanceIndices);
		classifier.buildClassifier(training);
		evaluation.evaluateModel(classifier, test);
	}

	/**
	 * Internal method for leave-subject-out cross-validation. Searches the
	 * indices for the test set.
	 * 
	 * @param subjectIDs
	 * @param subjectsPerHoldOut
	 * @param iterator
	 * @return
	 */
	private static List<Integer> getTestInstanceIndices(SubjectIDList subjectIDs, int subjectsPerHoldOut, Iterator<Integer> iterator) {
		List<Integer> testIndices = null;

		testIndices = new LinkedList<Integer>();
		for (int subjectIndex = 0; subjectIndex < subjectsPerHoldOut; subjectIndex++) {
			testIndices.addAll(subjectIDs.getIndicesForID(iterator.next()));
		}
		Collections.sort(testIndices);

		return testIndices;
	}

	/**
	 * Internal method for leave-subject-out cross-validation. Prepares the two
	 * sets: training and testing set.
	 * 
	 * @param instances
	 * @param training
	 * @param test
	 * @param testInstanceIndices
	 */
	private static void prepareTrainingAndTestSet(Instances instances, Instances training, Instances test, List<Integer> testInstanceIndices) {
		for (int instanceIndex = instances.numInstances() - 1; instanceIndex >= 0; instanceIndex--) {
			if (testInstanceIndices.contains(instanceIndex)) {
				training.delete(instanceIndex);
			} else {
				test.delete(instanceIndex);
			}
		}
	}

	/**
	 * Returns the difference of the two sets.
	 * 
	 * @param all
	 * @param subset
	 * @return
	 */
	public static Instances difference(Instances all, Instances subset) {
		Instances difference = null;
		Instance instance1 = null;
		Instance instance2 = null;
		boolean found = false;
		LinkedList<Instance> list = new LinkedList<Instance>();
		InstanceComparator comparator = new InstanceComparator(true);

		for (int i = 0; i < all.numInstances(); i++) {
			instance1 = all.instance(i);
			found = false;
			for (int j = 0; j < subset.numInstances(); j++) {
				instance2 = subset.instance(j);
				if (comparator.compare(instance1, instance2) == 0) {
					found = true;
				}
			}
			if (!found) {
				list.add(new Instance(instance1));
			}
		}

		difference = new Instances(all);
		difference.delete();
		for (Instance instance : list) {
			difference.add(instance);
		}

		return difference;
	}

	/**
	 * Returns two new instances according to the class labels. Only for
	 * two-class problems!
	 * 
	 * @param data
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Instances[] separateClasses(Instances data) {
		int classIndex;
		Attribute classAttribute = null;
		Enumeration enumeration = null;
		String classNameOne = null;
		Instances instancesOne = null;
		Instances instancesTwo = null;

		classIndex = data.classIndex();
		classAttribute = data.classAttribute();
		instancesOne = new Instances(data);
		instancesOne.delete();
		instancesTwo = new Instances(data);
		instancesTwo.delete();
		enumeration = data.classAttribute().enumerateValues();
		classNameOne = (String) enumeration.nextElement();

		for (int i = 0; i < data.numInstances(); i++) {
			if (classNameOne.equals(classAttribute.value((int) data.instance(i).value(classIndex)))) {
				instancesOne.add(data.instance(i));
			} else {
				instancesTwo.add(data.instance(i));
			}
		}

		return new Instances[] { instancesOne, instancesTwo };
	}

	/**
	 * Concatenates two sets of instances.
	 * 
	 * @param one
	 * @param two
	 * @return
	 */
	public static Instances concatenateInstances(Instances one, Instances two) {
		Instances all = null;

		all = new Instances(one);
		all.delete();
		for (int i = 0; i < one.numInstances(); i++) {
			all.add(one.instance(i));
		}
		for (int i = 0; i < two.numInstances(); i++) {
			all.add(two.instance(i));
		}
		return all;
	}

	public static Instances attributeSubset(Instances instances, int[] indicesOfAttributesToKeep) {
		Instances copy = new Instances(instances);

		for (int attributeIndex = instances.numAttributes() - 1; attributeIndex >= 0; attributeIndex--) {
			if (Arrays.binarySearch(indicesOfAttributesToKeep, attributeIndex) < 0) {
				copy.deleteAttributeAt(attributeIndex);
			}
		}
		return copy;
	}

	/**
	 * Evaluates the given classifier with the given testing instances.
	 * 
	 * @param trainingInstances
	 * @param testInstances
	 * @param trainedClassifier
	 * @param evaluation
	 * @throws Exception
	 */
	public static void evaluateWithTestInstances(Instances trainingInstances, Instances testInstances, Classifier trainedClassifier, Evaluation evaluation)
			throws Exception {
		boolean found;
		List<Integer> removeIndicies = new LinkedList<Integer>();
		Remove removeFilter = new Remove();

		if (testInstances.classIndex() == -1) {
			testInstances.setClassIndex(testInstances.numAttributes() - 1);
		}

		// feature selection may has removed some attributes
		// remove them from the test instances, too
		for (int i = 0; i < testInstances.numAttributes(); i++) {
			found = false;
			for (int j = 0; j < trainingInstances.numAttributes(); j++) {
				if (testInstances.attribute(i).name().equals(trainingInstances.attribute(j).name())) {
					found = true;
				}
			}
			if (!found) {
				removeIndicies.add(i);
			}
		}
		removeFilter.setAttributeIndicesArray(CommonUtilities.integerListToArray(removeIndicies));
		removeFilter.setInputFormat(testInstances);
		testInstances = Filter.useFilter(testInstances, removeFilter);

		evaluation.evaluateModel(trainedClassifier, testInstances);
	}

}
