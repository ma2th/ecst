package ecst.algorithm.featureextraction;

import ecst.algorithm.FeatureExtractionAlgorithm;

/**
 * This is the basis class that all feature extraction algorithms that work only
 * on one input column have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class OneColumnFeatureExtraction extends FeatureExtractionAlgorithm {

	private int column;

	/**
	 * This method has to return the number of features that will be computed.
	 * @param linesOfData
	 * @return
	 */
	public abstract int getNumberOfFeatures(int linesOfData);

	/**
	 * This method has to compute the feature(s).
	 * @param data
	 * @return
	 */
	protected abstract double[] computeFeaturesOnColumn(double[] data);

	/**
	 * Returns the number of features that will be computed.
	 */
	@Override
	public int getNumberOfFeatures(int columnsOfData, int linesOfData) {
		column = Integer.parseInt((String) getColumnsForFeatureExtraction().getValue());
		column--;
		return getNumberOfFeatures(linesOfData);
	}

	/**
	 * Computes the features.
	 */
	public double[] computeFeatures(double[][] data) {
		if (data == null || data.length == 0 || data[column].length == 0) {
			return null;
		}
		return computeFeaturesOnColumn(data[column]);
	}
}
