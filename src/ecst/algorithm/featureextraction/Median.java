package ecst.algorithm.featureextraction;

import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.MathUtilities;

/**
 * This class implements the median feature extraction algorithm.
 * 
 * @author Matthias Ring
 *
 */
public class Median extends OneColumnFeatureExtraction {
	
	private int linesOfData;

	/**
	 * Returns the name of the implementing class (= this)
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return Median.class;
	}
	
	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initFeatureExtractionParameters() {
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	protected Parameter[] getFeatureExtractionParameters() {
		return null;
	}

	/** 
	 * Returns the number of features that will be computed.
	 */
	@Override
	public int getNumberOfFeatures(int linesOfData) {
		this.linesOfData = linesOfData;
		return 1;
	}

	/**
	 * Computes the feature.
	 */
	@Override
	protected double[] computeFeaturesOnColumn(double[] data) {
		return new double[] { MathUtilities.createDescriptiveStatistics(data).getPercentile(50) };
	}

	/**
	 * Adds the additional complexity measures if the number of lines is equal.
	 */
	@Override
	protected void addDependencies() {
		if (linesOfData % 2 == 0) {
			addDependency("evenLinesPerFeature");
		}
	}

	/**
	 * No algorihtm-specific multipliers here.
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
