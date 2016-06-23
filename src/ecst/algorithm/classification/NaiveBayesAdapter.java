package ecst.algorithm.classification;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of the Naive Bayes
 * classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class NaiveBayesAdapter extends ClassificationAlgorithm {
	
	private Parameter useKernelEstimator;
	private Parameter useSupervisedDiscretization;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		useKernelEstimator = new Parameter(false, "Use kernel estimator", Parameter.TYPE.BOOLEAN, "-K");
		useSupervisedDiscretization = new Parameter(false, "Use supervised discretization", Parameter.TYPE.BOOLEAN, "-D");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { useKernelEstimator, useSupervisedDiscretization };
	}

	/**
	 * Returns the grid search parameters of this algorithm.
	 */
	@Override
	protected Parameter[] getGridSearchParameters() {
		return null;
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return NaiveBayes.class;
	}

	/**
	 * Nothing to postprocess here.
	 */
	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	/**
	 * Nothing to do here.
	 */
	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
	}

	/**
	 * No dependencies for this algoritm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * No algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}
	
	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
