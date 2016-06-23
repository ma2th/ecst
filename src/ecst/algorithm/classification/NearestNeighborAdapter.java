package ecst.algorithm.classification;

import weka.classifiers.Classifier;
import weka.classifiers.lazy.IBk;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of the Nearest Neighbor
 * classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class NearestNeighborAdapter extends ClassificationAlgorithm {
	
	private Parameter windowSize;
	private Parameter useSquaredError;
	private Parameter numberOfNeighbors;
	private Parameter specifyKByEvaluation;
	private int k;
	private int numberOfInstances;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		numberOfNeighbors = new Parameter(1, "Number of nearest neighbors (k)", Parameter.TYPE.INTEGER, "-K");
		useSquaredError = new Parameter(false, "Use mean squared rather than mean absolute error when specifing k", Parameter.TYPE.BOOLEAN,
				"-E");
		windowSize = new Parameter(0, "Maximum number of training instances maintained", Parameter.TYPE.INTEGER, "-W");
		specifyKByEvaluation = new Parameter(false, "Specify number of nearest neighbors (k) by evaluation on training data",
				Parameter.TYPE.BOOLEAN, "-X");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { numberOfNeighbors, useSquaredError, windowSize, specifyKByEvaluation };
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
		return IBk.class;
	}

	/**
	 * Nothing to postprocess here.
	 */
	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}


	/**
	 * Analyzes the trained classifier to perform the complexity analysis later.
	 */
	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
		numberOfInstances = data.getFeatureSelectedInstances().numInstances();
		k = ((IBk) classifier).getKNN();
	}

	/**
	 * No dependencies for this algoritm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * Sets the factors of the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if ("nearestNeighors".equals(multiplier.getName())) {
			multiplier.setFactor(k);
		} else if ("instancesMinusNearestNeighors".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfInstances - k);
		}
	}
	
	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
