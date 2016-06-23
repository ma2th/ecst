package ecst.combiner;

import java.util.LinkedList;
import java.util.List;

import weka.core.Attribute;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/**
 * This class represents the data in the different steps of the pattern
 * recognition pipeline.
 * 
 * @author Matthias Ring
 * 
 */
public class PipelineData {

	private Instances instances;
	private Instances preprocessedInstances;
	private Instances featureSelectedInstances;
	private List<String> userSelectedAttributes;
	private List<String> attributesAfterTraining;
	private SubjectIDList subjectIDs;
	private SubjectIDList preprocessedSubjectIDs;

	/**
	 * Constructor. Removes the subject IDs if presents and saves them in an
	 * additional list.
	 * 
	 * @param instances
	 * @throws Exception
	 */
	public PipelineData(Instances instances) throws Exception {
		Attribute attribute = null;
		Remove remove = null;

		userSelectedAttributes = new LinkedList<String>();
		attributesAfterTraining = new LinkedList<String>();
		setClassAttribute(instances);

		attribute = instances.attribute("Subject ID");
		if (attribute != null) {
			subjectIDs = new SubjectIDList();
			for (int i = 0; i < instances.numInstances(); i++) {
				subjectIDs.add(Integer.parseInt(instances.instance(i).stringValue(attribute)));
			}
			remove = new Remove();
			remove.setAttributeIndicesArray(new int[] { attribute.index() });
			remove.setInputFormat(instances);
			this.instances = Filter.useFilter(instances, remove);
			this.preprocessedSubjectIDs = subjectIDs;
		} else {
			this.instances = instances;
		}
	}

	/**
	 * Returns the number of instances left after the trainnig phase.
	 * 
	 * @return
	 */
	public int getNumberOfInstancesAfterTraining() {
		return featureSelectedInstances.numInstances();
	}

	/**
	 * Returns the number of classes left after the training phase.
	 * 
	 * @return
	 */
	public int getNumberOfClassesAfterTraining() {
		return featureSelectedInstances.numClasses();
	}

	/**
	 * Returns the number of attributes left after the training phase.
	 * 
	 * @return
	 */
	public int getNumberOfAttributesAfterTraining() {
		return attributesAfterTraining.size();
	}

	/**
	 * Return the number of user selected attributes left after the training
	 * phase.
	 * 
	 * @return
	 */
	public int getNumberOfUserSelectedAttributesAfterTraining() {
		int counter = 0;

		for (String userSelected : userSelectedAttributes) {
			if (attributesAfterTraining.contains(userSelected)) {
				counter++;
			}
		}

		return counter;
	}

	/**
	 * Sets the index of the class attribute to the last attribute.
	 * 
	 * @param instances
	 */
	private void setClassAttribute(Instances instances) {
		if (instances.classIndex() == -1) {
			instances.setClassIndex(instances.numAttributes() - 1);
		}
	}

	/**
	 * Returns the instances before the training phase.
	 * 
	 * @return
	 */
	public Instances getInstances() {
		return instances;
	}

	/**
	 * Return the instances after the preprocessing step.
	 * 
	 * @return
	 */
	public Instances getPreprocessedInstances() {
		return preprocessedInstances;
	}

	/**
	 * Sets the instances after the preprocessing step.
	 * 
	 * @param preprocessedInstances
	 */
	public void setPreprocessedInstances(Instances preprocessedInstances) {
		this.preprocessedInstances = preprocessedInstances;
		setClassAttribute(this.preprocessedInstances);
	}

	/**
	 * Returns the instances after the feature selection step.
	 * 
	 * @return
	 */
	public Instances getFeatureSelectedInstances() {
		return featureSelectedInstances;
	}

	/**
	 * Sets the instances after the feature selection step.
	 * 
	 * @param featureSelectedInstances
	 */
	public void setFeatureSelectedInstances(Instances featureSelectedInstances) {
		this.featureSelectedInstances = featureSelectedInstances;
		setClassAttribute(this.featureSelectedInstances);

		for (int i = 0; i < this.featureSelectedInstances.numAttributes(); i++) {
			if (i != this.featureSelectedInstances.classIndex()) {
				attributesAfterTraining.add(this.featureSelectedInstances.attribute(i).name());
			}
		}
	}

	/**
	 * Returns the user selected attributes.
	 * 
	 * @return
	 */
	public List<String> getUserSelectedAttributes() {
		return userSelectedAttributes;
	}

	/**
	 * Returns the list of attributes left after the training phase.
	 * 
	 * @return
	 */
	public List<String> getAttributesAfterTraining() {
		return attributesAfterTraining;
	}

	/**
	 * Returns the subject IDs list.
	 * 
	 * @return
	 */
	public SubjectIDList getSubjectIDs() {
		return subjectIDs;
	}

	/**
	 * Returns the subject IDs list after the preprocessing step.
	 * 
	 * @return
	 */
	public SubjectIDList getPreprocessedSubjectIDs() {
		return preprocessedSubjectIDs;
	}

	/**
	 * Sets the subject IDs list after the preprocessing step.
	 * 
	 * @param preprocessedSubjectIDs
	 */
	public void setPreprocessedSubjectIDs(SubjectIDList preprocessedSubjectIDs) {
		this.preprocessedSubjectIDs = preprocessedSubjectIDs;
	}

}
