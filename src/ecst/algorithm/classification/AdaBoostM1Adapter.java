package ecst.algorithm.classification;

import weka.classifiers.Classifier;
import weka.classifiers.meta.AdaBoostM1;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of the AdaBoost.M1
 * classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class AdaBoostM1Adapter extends ClassificationAlgorithm {

	private Parameter seed;
	private Parameter weightMass;
	private Parameter useResampling;
	private Parameter numOfIterations;
	private int weakLeaners;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		seed = new Parameter(1, "Random number seed", Parameter.TYPE.INTEGER, "-S");
		weightMass = new Parameter(100, "Percentage of weight mass to base training on", Parameter.TYPE.INTEGER, "-P");
		useResampling = new Parameter(false, "Use resampling for boosting", Parameter.TYPE.BOOLEAN, "-Q");
		numOfIterations = new Parameter(10, "Number of iterations", Parameter.TYPE.INTEGER, "-I");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { seed, weightMass, useResampling, numOfIterations };
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
		return AdaBoostM1.class;
	}

	/**
	 * Analyzes the trained classifier to perform the complexity analysis later.
	 */
	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
		weakLeaners = (Integer) numOfIterations.getValue();
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
		if ("weakLeaners".equals(multiplier.getName())) {
			multiplier.setFactor(weakLeaners);
		} else if ("weakLeanersMinusOne".equals(multiplier.getName())) {
			multiplier.setFactor(weakLeaners - 1);
		}
	}

	/**
	 * Nothing to postprocess here.
	 */
	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
