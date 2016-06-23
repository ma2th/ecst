package ecst.algorithm.preprocessing;

import weka.core.Attribute;
import weka.core.Instances;
import weka.core.Range;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.InterquartileRange;
import weka.filters.unsupervised.attribute.Remove;
import weka.filters.unsupervised.instance.SubsetByExpression;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.combiner.SubjectIDList;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the WEKA implementation of the interquartile
 * range outlier detection.
 * 
 * @author Matthias Ring
 * 
 */
public class InterquartileRangeAdapter extends PreprocessingAlgorithm {

	private Parameter outlierFactor;
	private Parameter columnsForOutlierDetection;

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return InterquartileRange.class;
	}

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	public void initParameters() {
		columnsForOutlierDetection = new Parameter("first-last", "List of columns to base outlier value detection on",
				Parameter.TYPE.STRING, "-R");
		outlierFactor = new Parameter(3, "Factor for outlier detection", Parameter.TYPE.INTEGER, "-O");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { columnsForOutlierDetection, outlierFactor };
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
	 * Performs the preprocessing with the WEKA implementation and removes the
	 * outlier instances.
	 */
	@Override
	public Instances filterData(PipelineData data) throws Exception {
		int[] selectedIndices = null;
		int numAttributes = 0;
		boolean first = true;
		boolean keepInstance;
		Attribute attribute = null;
		String optionsString = null;
		String expression = "";
		Instances outlierDetectedInstances = null;
		Instances subsetInstances = null;
		Instances cleanedInstances = null;
		InterquartileRange interquartileRange = new InterquartileRange();
		SubsetByExpression subsetByExpression = new SubsetByExpression();
		Remove remove = new Remove();
		Range selectedRange = new Range();
		SubjectIDList subjectIDs = null;
		SubjectIDList cleanedSubjectIDs = null;

		numAttributes = data.getInstances().numAttributes();
		optionsString = ParameterUtilities.buildOptionsString(getParameters());
		optionsString += " -P -E-as-O";
		interquartileRange.setOptions(Utils.splitOptions(optionsString));
		interquartileRange.setInputFormat(data.getInstances());
		outlierDetectedInstances = Filter.useFilter(data.getInstances(), interquartileRange);

		// remove outlier subject IDs
		subjectIDs = data.getSubjectIDs();
		if (subjectIDs != null) {
			cleanedSubjectIDs = new SubjectIDList();
			for (int instanceIndex = 0; instanceIndex < outlierDetectedInstances.numInstances(); instanceIndex++) {
				keepInstance = true;
				for (int attributeIndex = numAttributes; attributeIndex < outlierDetectedInstances.numAttributes(); attributeIndex++) {
					attribute = outlierDetectedInstances.attribute(attributeIndex);
					if (attribute.isNominal() && attribute.name().endsWith("_Outlier")) {
						if (outlierDetectedInstances.instance(instanceIndex).stringValue(attribute).equals("yes")) {
							keepInstance = false;
							break;
						}
					}
				}
				if (keepInstance) {
					cleanedSubjectIDs.add(subjectIDs.get(instanceIndex));
				}
			}
			data.setPreprocessedSubjectIDs(cleanedSubjectIDs);
		}

		// remove outlier
		for (int i = numAttributes; i < outlierDetectedInstances.numAttributes(); i++) {
			attribute = outlierDetectedInstances.attribute(i);
			if (attribute.isNominal() && attribute.name().endsWith("_Outlier")) {
				if (!first) {
					expression += "and";
				} else {
					first = false;
				}
				expression += " (ATT" + (i + 1) + " is 'no') ";
			}
		}

		subsetByExpression.setOptions(Utils.splitOptions("-E \"" + expression.trim() + "\""));
		subsetByExpression.setInputFormat(outlierDetectedInstances);
		subsetInstances = Filter.useFilter(outlierDetectedInstances, subsetByExpression);

		// remove attributes created by the InterquartileRange filter
		remove.setOptions(Utils.splitOptions("-R " + (numAttributes + 1) + "-last"));
		remove.setInputFormat(subsetInstances);
		cleanedInstances = Filter.useFilter(subsetInstances, remove);

		selectedRange.setRanges((String) columnsForOutlierDetection.getValue());
		selectedRange.setUpper(data.getInstances().numAttributes() - 1);
		selectedIndices = selectedRange.getSelection();
		for (int i = 0; i < selectedIndices.length; i++) {
			data.getUserSelectedAttributes().add(data.getInstances().attribute(selectedIndices[i]).name());
		}

		return cleanedInstances;
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}
	
	@Override
	protected String getPreprocessingModel(PipelineData data) {
		return "not yet implemented";
	}

}
