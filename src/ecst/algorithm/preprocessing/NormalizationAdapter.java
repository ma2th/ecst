package ecst.algorithm.preprocessing;

import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Normalize;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.FileUtilities;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the WEKA implementation of the normalization.
 * 
 * @author Matthias Ring
 * 
 */
public class NormalizationAdapter extends PreprocessingAlgorithm {

	private Parameter scale;
	private Parameter translation;
	private Parameter useUnsetClass;
	private double[] minimum;
	private double[] maximum;

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return Normalize.class;
	}

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	public void initParameters() {
		scale = new Parameter(1.0, "Scale", Parameter.TYPE.DOUBLE, "-S");
		translation = new Parameter(0.0, "Translation", Parameter.TYPE.DOUBLE, "-T");
		useUnsetClass = new Parameter(false, "Unset class index temporarily", Parameter.TYPE.BOOLEAN, "-unset-class-temporarily");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { scale, translation, useUnsetClass };
	}

	/**
	 * Performs the normalization.
	 */
	@Override
	public Instances filterData(PipelineData data) throws Exception {
		Instances instances = null;
		Normalize normalization = new Normalize();

		normalization.setOptions(Utils.splitOptions(ParameterUtilities.buildOptionsString(getParameters())));
		normalization.setInputFormat(data.getInstances());

		instances = Filter.useFilter(data.getInstances(), normalization);

		minimum = normalization.getMinArray();
		maximum = normalization.getMaxArray();

		return instances;
	}

	/**
	 * Nothing to do here.
	 */
	@Override
	protected void analyzeSystem(PipelineData data) throws Exception {
	}

	/**
	 * No dependencies in this algorithm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * No algorithm-specific multipliers in this algorithm.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}

	/**
	 * Exports the model determined in the training phase to XML.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		StringBuilder builder = new StringBuilder();

		builder.append("<minimum>");
		dumpArray(minimum, builder, data);
		builder.append("</minimum>\n");
		builder.append("<maximum>\n");
		dumpArray(maximum, builder, data);
		builder.append("</maximum>\n");

		return builder.toString();
	}

	/**
	 * Internal method to export an array to XML.
	 * 
	 * @param arr
	 * @param builder
	 * @param data
	 */
	private void dumpArray(double[] arr, StringBuilder builder, PipelineData data) {
		for (int i = 0; i < data.getInstances().numAttributes(); i++) {
			if (data.getAttributesAfterTraining().contains(data.getInstances().attribute(i).name())) {
				builder.append("<attribute name=\"");
				builder.append(FileUtilities.exportXMLString(data.getInstances().attribute(i).name()));
				builder.append("\" value=\"");
				builder.append(FileUtilities.exportXMLString("" + arr[i]));
				builder.append("\"/>\n");
			}
		}
	}

	protected String getPreprocessingModel(PipelineData data) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < data.getInstances().numAttributes() - 1; i++) {
			builder.append(data.getInstances().attribute(i).name() + ": min " + minimum[i] + ", max: " + maximum[i] + "\n");
		}

		return builder.toString();
	}
}
