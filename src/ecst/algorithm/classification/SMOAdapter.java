package ecst.algorithm.classification;

import weka.classifiers.Classifier;
import weka.classifiers.functions.SMO;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

public class SMOAdapter extends ClassificationAlgorithm {

	private Parameter parameterC;
	private Parameter tolerance;
	private Parameter normalize;
	private Parameter epsilon;
	private Parameter logisticModel;
	private Parameter folds;
	private Parameter random;
	private Parameter kernel;
	private Parameter polyKernelExponent;
	private Parameter polyKernelLowerOrder;
	private Parameter mercerSigmoidKernelB;
	private Parameter mercerSigmoidKernelD;
	private SelectedParameterItem mercerSigmoidSelectedItem;
	private SelectedParameterItem polySelectedItem;
	private SelectedParameterItem normalizeSelectedItem;
	private SelectedParameterItem standardizeSelectedItem;
	private SelectedParameterItem noneSelectedItem;

	@Override
	public Class<? extends Object> getImplementingClass() {
		return SMO.class;
	}

	@Override
	protected void initClassifierParameters() {
		parameterC = new Parameter(1.0, "Parameter C", Parameter.TYPE.DOUBLE, "-C");
		tolerance = new Parameter(1.0e-3, "Tolerance parameter", Parameter.TYPE.DOUBLE, "-L");
		epsilon = new Parameter(1.0e-12, "Epsilon for round-off error", Parameter.TYPE.DOUBLE, "-P");
		logisticModel = new Parameter(false, "Fit logistic models to SVM outputs", Parameter.TYPE.BOOLEAN, "-M");
		folds = new Parameter(-1, "Number of folds for the internal cross-validation", Parameter.TYPE.INTEGER, "-V");
		random = new Parameter(1, "Random number seed", Parameter.TYPE.INTEGER, "-W");

		mercerSigmoidSelectedItem = new SelectedParameterItem("Mercer sigmoid (Carrington, EMBC 2014)", "-K ecst.algorithm.classification.kernel.MercerSigmoidKernel");
		polySelectedItem = new SelectedParameterItem("Polynomial", "-K weka.classifiers.functions.supportVector.PolyKernel");
		kernel = ParameterUtilities.createSelectedParameter("Kernel", mercerSigmoidSelectedItem, polySelectedItem);

		polyKernelLowerOrder = new Parameter(false, "Polynomial kernel: use lower order terms", Parameter.TYPE.BOOLEAN, "-L", null, polySelectedItem);
		polyKernelExponent = new Parameter(1.0, "Polynomial kernel: exponent", Parameter.TYPE.DOUBLE, "-E", null, polySelectedItem);

		mercerSigmoidKernelB = new Parameter(1.0, "Sigmoid Mercer kernel: b", Parameter.TYPE.DOUBLE, "-B", null, mercerSigmoidSelectedItem);
		mercerSigmoidKernelD = new Parameter(0.0, "Sigmoid Mercer kernel: d", Parameter.TYPE.DOUBLE, "-D", null, mercerSigmoidSelectedItem);

		normalizeSelectedItem = new SelectedParameterItem("Normalize", "-N 0");
		standardizeSelectedItem = new SelectedParameterItem("Standardize", "-N 1");
		noneSelectedItem = new SelectedParameterItem("None", "-N 2");
		normalize = ParameterUtilities.createSelectedParameter("Preprocessing", normalizeSelectedItem, standardizeSelectedItem, noneSelectedItem);
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { parameterC, tolerance, normalize, epsilon, logisticModel, folds, random, kernel, polyKernelLowerOrder, polyKernelExponent,
				mercerSigmoidKernelB, mercerSigmoidKernelD };
	}

	@Override
	protected Parameter[] getGridSearchParameters() {
		return null;
	}

	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
	}

	@Override
	protected void addDependencies() {
	}

	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}

	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
