package ecst.algorithm;

import ecst.algorithm.analysis.DynamicOperations;
import ecst.algorithm.analysis.DynamicSpace;
import ecst.algorithm.parameter.Parameter;
import ecst.utilities.ParameterUtilities;

/**
 * This is the basis class that all feature extraction algorithms have to
 * extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class FeatureExtractionAlgorithm extends WorkPhaseAlgorithm {

	private Parameter columnsForFeatureExtraction;

	/**
	 * This method has to initialize the parameters for this algorithm.
	 */
	protected abstract void initFeatureExtractionParameters();

	/**
	 * This method has to return all parameters of this algorithm.
	 * 
	 * @return
	 */
	protected abstract Parameter[] getFeatureExtractionParameters();

	/**
	 * This method has to return the number of features that will be generated
	 * by this algorithm.
	 * 
	 * @param columnsOfData
	 *            the number of columns for one feature computation
	 * @param linesOfData
	 *            the number of lines for one feature computation
	 * @return
	 */
	public abstract int getNumberOfFeatures(int columnsOfData, int linesOfData);

	/**
	 * This method has to compute the feature(s).
	 * 
	 * @param data
	 *            the sensor signals
	 * @return
	 */
	public abstract double[] computeFeatures(double[][] data);

	/**
	 * Initializes the parameters for this algorithm.
	 */
	@Override
	public void initParameters() {
		columnsForFeatureExtraction = new Parameter("1", "Columns for feature extraction", Parameter.TYPE.STRING, null);
		initFeatureExtractionParameters();
	}

	/**
	 * Returns all parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return ParameterUtilities.mergeParameters(new Parameter[] { columnsForFeatureExtraction }, getFeatureExtractionParameters());
	}

	/**
	 * Returns the columns that are the input for the feature computation.
	 * 
	 * @return
	 */
	protected Parameter getColumnsForFeatureExtraction() {
		return columnsForFeatureExtraction;
	}

	/**
	 * Returns a string representation of this algorithm.
	 */
	public String toString() {
		return getDefinition().getName() + " of column " + columnsForFeatureExtraction.getValue();
	}

	/**
	 * Extracts the features and performs the complexity analysis.
	 * 
	 * @param data
	 * @return
	 */
	public double[] extractFeatures(double[][] data) {
		double[] features = computeFeatures(data);
		saveAnalysis();
		return features;
	}

	/**
	 * Adds the attribute names that have to be present after feature selection
	 * if the operations and space of this algorithm should be added to the
	 * analysis.
	 * 
	 * @param attributeNames
	 */
	public void setDependsOnFeatures(String[] attributeNames) {
		for (DynamicOperations operations : getDynamicOperationsList()) {
			operations.setDependsOnFeatures(attributeNames);
		}
		for (DynamicSpace space : getDynamicSpaceList()) {
			space.setDependsOnFeatures(attributeNames);
		}
	}

}
