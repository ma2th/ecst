package ecst.algorithm.featureselection.evaluator;

import java.util.BitSet;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.SubsetEvaluator;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ecst.combiner.SubjectIDList;
import ecst.utilities.CommonUtilities;
import ecst.utilities.InstanceUtilities;

/**
 * This class evaluates feature subsets in feature selection algorithms that
 * evaluate with respect to subject IDs.
 * 
 * @author Matthias Ring
 * 
 */
public class LeaveSubjectOutEvaluator extends ASEvaluation implements SubsetEvaluator {

	private static final long serialVersionUID = 1L;

	private Instances instances;
	private Classifier classifier;
	private int subjectsPerHoldOut;
	private SubjectIDList subjectIDs;

	/**
	 * Constructor.
	 * 
	 * @param classifier
	 * @param subjectsPerHoldOut
	 * @param subjectIDs
	 */
	public LeaveSubjectOutEvaluator(Classifier classifier, int subjectsPerHoldOut, SubjectIDList subjectIDs) {
		if (subjectIDs == null) {
			throw new IllegalArgumentException("No subject IDs available!");
		}

		this.classifier = classifier;
		this.subjectIDs = subjectIDs;
		this.subjectsPerHoldOut = subjectsPerHoldOut;
	}

	/**
	 * Nothing to do here, just save the data.
	 */
	@Override
	public void buildEvaluator(Instances data) throws Exception {
		this.instances = data;
	}

	/**
	 * Evaluates the subset by performing a leave-subject-out cross-validation.
	 */
	@Override
	public double evaluateSubset(BitSet subset) throws Exception {
		Instances copy = null;

		copy = new Instances(instances);
		copy = Filter.useFilter(copy, initAttributesFilter(subset));
		return InstanceUtilities.evaluateLeaveSubjectOut(classifier, copy, subjectIDs, subjectsPerHoldOut).pctCorrect();
	}

	/**
	 * Internal method to remove the unused features.
	 * @param subset
	 * @return
	 * @throws Exception
	 */
	private Remove initAttributesFilter(BitSet subset) throws Exception {
		Remove attributesFilter = new Remove();
		int[] indices = CommonUtilities.bitsetToIntegerArray(subset, true);

		indices[indices.length - 1] = instances.classIndex();
		attributesFilter.setAttributeIndicesArray(indices);
		attributesFilter.setInvertSelection(true);
		attributesFilter.setInputFormat(instances);
		return attributesFilter;
	}

}
