package ecst.algorithm.evaluation;

import java.util.Random;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of a cross-validation.
 * 
 * @author Matthias Ring
 * 
 */
public class CrossValidationAdapter extends EvaluationAlgorithm {

	private Parameter folds;
	private Parameter randomNumberSeed;

	/**
	 * Returns the class implementing the cross-validation.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return CrossValidationAdapter.class;
	}

	/**
	 * Returns a string describing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Performs a cross validation. For more information, see\n\n"
				+ "R. Duda, P. Hart and D. Stork, Pattern classification, Wiley, 2001.";
	}

	/**
	 * Initializes all paramters.
	 */
	@Override
	public void initParameters() {
		randomNumberSeed = new Parameter(1L, "Random number seed", Parameter.TYPE.LONG, null);
		folds = new Parameter(10, "Folds", Parameter.TYPE.INTEGER, null);
	}

	/**
	 * Returns all paramters.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { folds, randomNumberSeed };
	}

	/**
	 * Evaluates the given classifier for the given pipeline data.
	 * Performs an inner feature selection if the argument is not null.
	 */
	@Override
	public Evaluation evaluate(PipelineData data, Classifier classifier, FeatureSelectionAlgorithm featureSelection) throws Exception {
		AttributeSelectedClassifier metaClassifier = null;
		Evaluation evaluation = new Evaluation(data.getPreprocessedInstances());

		if (featureSelection != null) {
			metaClassifier = new AttributeSelectedClassifier();
			metaClassifier.setEvaluator(featureSelection.createEvaluator(classifier, data));
			metaClassifier.setSearch(featureSelection.createSearchMethod());
			metaClassifier.setClassifier(classifier);
			evaluation.crossValidateModel(metaClassifier, data.getPreprocessedInstances(), (Integer) folds.getValue(), new Random(
					(Long) randomNumberSeed.getValue()));
		} else {
			evaluation.crossValidateModel(classifier, data.getPreprocessedInstances(), (Integer) folds.getValue(), new Random(
					(Long) randomNumberSeed.getValue()));
		}
		return evaluation;
	}

}
