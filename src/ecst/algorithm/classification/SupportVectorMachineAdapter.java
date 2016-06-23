package ecst.algorithm.classification;

import java.lang.reflect.Field;

import org.apache.commons.math3.util.FastMath;

import libsvm.svm_model;
import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * This class is a adapter for the WEKA implementation of the SVM classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class SupportVectorMachineAdapter extends ClassificationAlgorithm {

	private Parameter svmType;
	private Parameter kernelType;
	private Parameter kernelDegree;
	private Parameter kernelGamma;
	private Parameter kernelCoefficient;
	private Parameter parameterC;
	private Parameter parameterNu;
	private Parameter tolerance;
	private Parameter weight;
	private SelectedParameterItem linearKernel;
	private SelectedParameterItem polynomialKernel;
	private SelectedParameterItem radialBasisKernel;
	private SelectedParameterItem sigmoidKernel;
	private SelectedParameterItem cSVC;
	private SelectedParameterItem nuSVC;
	private int numberOfSupportVectors;
	private int numberOfClasses;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		linearKernel = new SelectedParameterItem("Linear kernel", "-K 0");
		polynomialKernel = new SelectedParameterItem("Polynomial kernel", "-K 1");
		radialBasisKernel = new SelectedParameterItem("Radial basis kernel", "-K 2");
		sigmoidKernel = new SelectedParameterItem("Sigmoid kernel", "-K 3");
		cSVC = new SelectedParameterItem("C-SVC", "-S 0");
		nuSVC = new SelectedParameterItem("&nu;-SVC", "-S 1");
		svmType = ParameterUtilities.createSelectedParameter("SVM type", cSVC, nuSVC);
		kernelType = ParameterUtilities.createSelectedParameter("Kernel type", linearKernel, polynomialKernel, radialBasisKernel,
				sigmoidKernel);
		kernelDegree = new Parameter(3, "Degree in kernel function", Parameter.TYPE.INTEGER, "-D", "classifier.degree");
		kernelGamma = new Parameter(0.5, "&gamma; in kernel function", Parameter.TYPE.DOUBLE, "-G", "classifier.gamma");
		kernelCoefficient = new Parameter(0.0, "Coefficient in kernel function", Parameter.TYPE.DOUBLE, "-R", "classifier.coef0");
		parameterC = new Parameter(1.0, "Parameter C", Parameter.TYPE.DOUBLE, "-C", "classifier.cost");
		parameterNu = new Parameter(0.5, "Parameter &nu;", Parameter.TYPE.DOUBLE, "-N", "classifier.nu");
		tolerance = new Parameter(0.001, "Tolerance of termination criterion", Parameter.TYPE.DOUBLE, "-E");
		weight = new Parameter(null, "Weight, set C of class i to (weight &times; C)", Parameter.TYPE.STRING, "-W");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { weight, tolerance, svmType, kernelType, kernelDegree, kernelGamma, kernelCoefficient, parameterC,
				parameterNu };
	}

	/**
	 * Returns the grid search parameters of this algorithm.
	 */
	@Override
	public Parameter[] getGridSearchParameters() {
		return new Parameter[] { kernelCoefficient, kernelDegree, kernelGamma, parameterC, parameterNu };
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return LibSVM.class;
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
		Field modelField = null;

		modelField = LibSVM.class.getDeclaredField("m_Model");
		modelField.setAccessible(true);
		numberOfSupportVectors = (int) ((svm_model) modelField.get(classifier)).l;
		numberOfClasses = data.getFeatureSelectedInstances().numClasses();
	}

	/**
	 * No dependencies for this algoritm.
	 */
	@Override
	protected void addDependencies() {
		SelectedParameter selectedParameter = (SelectedParameter) kernelType.getValue();
		SelectedParameterItem item = selectedParameter.getItems().get(selectedParameter.getSelectedIndex());

		if (linearKernel.equals(item)) {
			addDependency("linearKernel");
		} else if (polynomialKernel.equals(item)) {
			addDependency("polynomialKernel");
		} else if (radialBasisKernel.equals(item)) {
			addDependency("radialBasisKernel");
		} else if (sigmoidKernel.equals(item)) {
			addDependency("sigmoidKernel");
		}
	}

	/**
	 * Sets the factors of the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if ("supportVectors".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfSupportVectors);
		} else if ("supportVectorsMinusOne".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfSupportVectors - 1);
		} else if ("supportVectorMachines".equals(multiplier.getName())) {
			multiplier.setFactor((numberOfClasses * (numberOfClasses - 1)) / 2);
		} else if ("squareAndMultiplyDegree".equals(multiplier.getName())) {
			multiplier.setFactor((int) (2.0 * FastMath.floor(FastMath.log(2.0, ((Integer) kernelDegree.getValue())))));
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
