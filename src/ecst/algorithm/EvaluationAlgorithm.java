package ecst.algorithm;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import ecst.combiner.PipelineData;

/**
 * This is the basis class that all evaluation algorithms have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class EvaluationAlgorithm extends Algorithm {

	/**
	 * This method has to perform the evaluation and return the result in form
	 * of a WEKA Evaluation object.
	 * 
	 * @param data the data for the pattern recognition pipeline
	 * @param classifier the trained classifier
	 * @param featureSelection the employed feature selection algorithm
	 * @return
	 * @throws Exception
	 */
	public abstract Evaluation evaluate(PipelineData data, Classifier classifier, FeatureSelectionAlgorithm featureSelection)
			throws Exception;

}
