package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.WrapperSubsetEval;
import weka.classifiers.Classifier;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.featureselection.evaluator.LeaveSubjectOutEvaluator;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * This class is the basis class that all algorithm which use the wrapper
 * appraoch have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class WrapperFeatureSelection extends FeatureSelectionAlgorithm {

	private Parameter seed;
	private Parameter crossValidationFolds;
	private Parameter crossValidationThreshold;
	private Parameter subjectsPerLeaveOut;
	private Parameter validationMethod;
	private SelectedParameterItem crossValidationEvaluation;
	private SelectedParameterItem leaveSubjectOutEvaluation;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initEvaluatorParameters() {
		// WrapperSubsetEval parameter
		seed = new Parameter(1, "Random number seed", Parameter.TYPE.INTEGER, null);
		crossValidationFolds = new Parameter(5, "Cross validation folds", Parameter.TYPE.INTEGER, null);
		crossValidationThreshold = new Parameter(0.01, "Threshold to execute another cross validation", Parameter.TYPE.DOUBLE, null);

		// LeaveSubjectOut parameter
		subjectsPerLeaveOut = new Parameter(1, "Number of subjects to leave out (set to 1 for 'leave one subject out' validation)",
				Parameter.TYPE.INTEGER, null);
		leaveSubjectOutEvaluation = new SelectedParameterItem("Leave subject out validation", "");
		crossValidationEvaluation = new SelectedParameterItem("Cross validation", "");
		validationMethod = ParameterUtilities.createSelectedParameter(
				"Validation method (for leave-subject-out the data has to contains a column 'Subject ID')", crossValidationEvaluation,
				leaveSubjectOutEvaluation);
	}

	/**
	 * Returns the algorithm parameters.
	 */
	@Override
	protected Parameter[] getEvaluatorParameters() {
		return new Parameter[] { crossValidationThreshold, seed, crossValidationFolds, validationMethod, subjectsPerLeaveOut };
	}

	/**
	 * Creates a new instance of the evaluator that this algorithm uses.
	 */
	@Override
	public ASEvaluation createEvaluator(Classifier classifier, PipelineData data) {
		ASEvaluation evaluator = null;
		SelectedParameter selectedParameter = (SelectedParameter) validationMethod.getValue();
		SelectedParameterItem item = selectedParameter.getItems().get(selectedParameter.getSelectedIndex());

		if (leaveSubjectOutEvaluation.equals(item)) {
			evaluator = new LeaveSubjectOutEvaluator(classifier, (Integer) subjectsPerLeaveOut.getValue(), data.getPreprocessedSubjectIDs());
		} else {
			evaluator = new WrapperSubsetEval();
			if (crossValidationFolds.getValue() != null) {
				((WrapperSubsetEval) evaluator).setFolds((Integer) crossValidationFolds.getValue());
			}
			if (seed.getValue() != null) {
				((WrapperSubsetEval) evaluator).setSeed((Integer) seed.getValue());
			}
			if (crossValidationThreshold.getValue() != null) {
				((WrapperSubsetEval) evaluator).setThreshold((Double) crossValidationThreshold.getValue());
			}
			((WrapperSubsetEval) evaluator).setClassifier(classifier);
		}

		return evaluator;
	}

}
