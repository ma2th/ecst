package ecst.algorithm.classification;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LinearRegression;
import weka.classifiers.meta.ClassificationViaRegression;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * This class is a adapter for the WEKA implementation of the LDA classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class LinearRegressionAdapter extends ClassificationAlgorithm {

	private Parameter ridgeParameter;
	private Parameter noEliminateColinearAttributes;
	private Parameter selectionMethod;
	private Parameter regressionMethod;
	private SelectedParameterItem none;
	private SelectedParameterItem greedy;
	private SelectedParameterItem m5;
	private SelectedParameterItem linearRegression;
	private double[][] coefficients;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		none = new SelectedParameterItem("None", "-S 1");
		greedy = new SelectedParameterItem("Greedy", "-S 2");
		m5 = new SelectedParameterItem("M5", "-S 0");
		selectionMethod = ParameterUtilities.createSelectedParameter("Attribute selection method", m5, none, greedy);
		ridgeParameter = new Parameter(1.0e-8, "Ridge parameter", Parameter.TYPE.DOUBLE, "-R");
		noEliminateColinearAttributes = new Parameter(false, "Do not try to eliminate colinear attributes", Parameter.TYPE.BOOLEAN, "-C");
		linearRegression = new SelectedParameterItem("Linear regression", "-W weka.classifiers.functions.LinearRegression -- ");
		regressionMethod = ParameterUtilities.createSelectedParameter("Regression method", linearRegression);
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		// regressionMethod has to be the first parameter
		return new Parameter[] { regressionMethod, noEliminateColinearAttributes, ridgeParameter, selectionMethod };
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
		return ClassificationViaRegression.class;
	}

	/**
	 * Removes the features that were not selected by the internal LDA feature
	 * selection algorithm.
	 */
	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
		Field classifiersField = null;
		Classifier[] classifiers = null;
		String selectedAttribute = null;
		Set<String> selectedAttributes = new HashSet<String>();
		Iterator<String> iterator = null;

		classifiersField = ClassificationViaRegression.class.getDeclaredField("m_Classifiers");
		classifiersField.setAccessible(true);
		classifiers = ((Classifier[]) classifiersField.get(classifier));

		coefficients = new double[classifiers.length][];

		for (int j = 0; j < classifiers.length; j++) {
			coefficients[j] = ((LinearRegression) classifiers[j]).coefficients();
			for (int i = 0; i < coefficients[j].length - 1; i++) {
				if (coefficients[j][i] != 0.0) {
					selectedAttributes.add(data.getFeatureSelectedInstances().attribute(i).name());
				}
			}
		}

		iterator = data.getAttributesAfterTraining().iterator();
		while (iterator.hasNext()) {
			selectedAttribute = iterator.next();
			if (!selectedAttributes.contains(selectedAttribute)) {
				iterator.remove();
			}
		}
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
	 * Returns a XML string that represents this trained classifier.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < coefficients.length; i++) {
			builder.append("<coefficients classIndex=\"");
			builder.append(i);
			builder.append("\">\n");
			for (int j = 0; j < data.getFeatureSelectedInstances().numAttributes() - 1; j++) {
				if (data.getAttributesAfterTraining().contains(data.getFeatureSelectedInstances().attribute(j).name())) {
					builder.append("<coefficient attribute=\"");
					builder.append(data.getFeatureSelectedInstances().attribute(j).name());
					builder.append("\" value=\"");
					builder.append(coefficients[i][j]);
					builder.append("\"/>\n");
				}
			}
			builder.append("<coefficient attribute=\"constant\" value=\"");
			builder.append(coefficients[i][coefficients[i].length - 1]);
			builder.append("\"/>\n");
			builder.append("</coefficients>");
		}

		return builder.toString();
	}

}
