package ecst.algorithm.evaluation;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.InstanceUtilities;

/**
 * This class implements a leave-subject-out cross-validation.
 * 
 * @author Matthias Ring
 * 
 */
public class LeaveSubjectOutAdapter extends EvaluationAlgorithm {

	private Parameter subjectsPerLeaveOut;

	/**
	 * Returns a string describing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Performs a leave subject out validation. For more information, see\n\n"
				+ "S. Theodoridis and K. Koutroumbas, Pattern recognition, Academic Press, 2006.";
	}

	/**
	 * Initializes all paramters.
	 */
	@Override
	public void initParameters() {
		subjectsPerLeaveOut = new Parameter(1, "Number of subjects to leave out (set to 1 for 'leave one subject out' validation)",
				Parameter.TYPE.INTEGER, null);
	}

	/**
	 * Returns all paramters.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { subjectsPerLeaveOut };
	}

	/**
	 * Returns the name of the implementing class (= this)
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return LeaveSubjectOutAdapter.class;
	}

	/**
	 * Evaluates the given classifier with the given pipeline data. Performs an
	 * inner feature selection if the argument is not null.
	 */
	@Override
	public Evaluation evaluate(PipelineData data, Classifier classifier, FeatureSelectionAlgorithm featureSelection) throws Exception {
		Classifier classifierCopy = Classifier.makeCopy(classifier);
		AttributeSelectedClassifier metaClassifier = new AttributeSelectedClassifier();

		if (data.getPreprocessedSubjectIDs() == null) {
			throw new IllegalArgumentException("No subject IDs available!");
		}

		if (subjectsPerLeaveOut.getValue() == null || ((Integer) subjectsPerLeaveOut.getValue()) < 1) {
			throw new IllegalArgumentException("Subjects per leave has to be greater than one!");
		}

		if (featureSelection != null) {
			metaClassifier.setEvaluator(featureSelection.createEvaluator(classifier, data));
			metaClassifier.setSearch(featureSelection.createSearchMethod());
			metaClassifier.setClassifier(classifierCopy);
			return InstanceUtilities.evaluateLeaveSubjectOut(metaClassifier, data.getPreprocessedInstances(),
					data.getPreprocessedSubjectIDs(), (Integer) subjectsPerLeaveOut.getValue());
		} else {
			return InstanceUtilities.evaluateLeaveSubjectOut(classifierCopy, data.getPreprocessedInstances(),
					data.getPreprocessedSubjectIDs(), (Integer) subjectsPerLeaveOut.getValue());
		}
	}

}
