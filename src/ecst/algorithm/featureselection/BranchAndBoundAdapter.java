package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.classifiers.Classifier;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.featureselection.evaluator.ProbabilisticDistanceEvaluator;
import ecst.algorithm.featureselection.evaluator.ReproducingKernelHilbertSpaceEvaluator;
import ecst.algorithm.featureselection.search.BranchAndBound;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the branch-and-bound algorithm implementation.
 * 
 * @author Matthias Ring
 * 
 */
public class BranchAndBoundAdapter extends FeatureSelectionAlgorithm {

	public static enum KERNEL {
		RBF, POLYNOMIAL
	};

	public static enum DISTANCE_MEASURE {
		BHATTACHARYYA, KL_DIVERGENCE, BETWEEN_CLASS_SCATTER_MATRIX
	}
	

	private Parameter subsetSize;
	private Parameter distanceMeasure;
	private Parameter kernel;
	private Parameter numberOfEigenvectors;
	private Parameter kernelParameter;
	private SelectedParameterItem linearKernel;
	private SelectedParameterItem rbfKernel;
	private SelectedParameterItem polynomialKernel;
	private SelectedParameterItem bhattacharyya;
	private SelectedParameterItem divergence;
	private SelectedParameterItem betweenClassScatterMatrix;
	
	private Parameter computation;
	private SelectedParameterItem chen_algorithm;
	private SelectedParameterItem narendraFukunaga_algorithm;
	private SelectedParameterItem somolPudilKittler_algorithm;
	private SelectedParameterItem nakariyakulCasasent_algorithm;
	
	private Parameter critFunctionMethod;
	private SelectedParameterItem default_crit;
	private SelectedParameterItem linear_crit;
	private SelectedParameterItem exponential_crit;
	private SelectedParameterItem parabola_crit;


	/**
	 * Initializes the algorithm's search parameters.
	 */
	@Override
	protected void initSearchMethodParameters() {
		subsetSize = new Parameter(3, "Size of feature subset", Parameter.TYPE.INTEGER, "-S");
	}

	/**
	 * Returns the algorithm's search parameters.
	 */
	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { subsetSize,computation,critFunctionMethod };
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return BranchAndBound.class;
	}

	/**
	 * Initializes the algorithm's evaluator parameters.
	 */
	@Override
	protected void initEvaluatorParameters() {
		bhattacharyya = new SelectedParameterItem("Bhattacharyya", "-B");
		divergence = new SelectedParameterItem("KL divergence", "-D");
		betweenClassScatterMatrix = new SelectedParameterItem("Class distance", "S");
		distanceMeasure = ParameterUtilities.createSelectedParameter("Distance measure", bhattacharyya, divergence, betweenClassScatterMatrix);

		linearKernel = new SelectedParameterItem("Linear", "-K 0");
		rbfKernel = new SelectedParameterItem("RBF", "-K 1");
		polynomialKernel = new SelectedParameterItem("Polynomial", "-K 2");
		kernel = ParameterUtilities.createSelectedParameter("Kernel", linearKernel, rbfKernel, polynomialKernel);

		numberOfEigenvectors = new Parameter(3, "Number of eigenvectors/-values", Parameter.TYPE.INTEGER, null);
		kernelParameter = new Parameter(0.5, "Kernel parameter (gamma for RBF kernel; exponent for polynomial kernel)", Parameter.TYPE.DOUBLE, null);
		
		chen_algorithm = new SelectedParameterItem("Chen, Pattern Recogn Lett, 24(12):1925-1933, 2003", "-T 1");
		narendraFukunaga_algorithm = new SelectedParameterItem("Narendra and Fukunaga, IEEE T Comput, C-26(9):917-922, 1977", "-T 2");
		somolPudilKittler_algorithm = new SelectedParameterItem("Somol, Pudil and Kittler, IEEE T Pattern Anal, 26(7):900–912, 2004", "-T 3");
		nakariyakulCasasent_algorithm = new SelectedParameterItem("Nakariyaku and Casasent, Pattern Recogn Lett, 28(12):1415–1427, 2007", "-T 4");
		computation = ParameterUtilities.createSelectedParameter("Search algorithm", chen_algorithm, narendraFukunaga_algorithm, somolPudilKittler_algorithm,
				nakariyakulCasasent_algorithm);
	
		default_crit = new SelectedParameterItem("default", "-J 1");
		linear_crit = new SelectedParameterItem("linear", "-J 2");
		exponential_crit = new SelectedParameterItem("exponential", "-J 3");
		parabola_crit = new SelectedParameterItem("parabola", "-J 4");
		critFunctionMethod = ParameterUtilities.createSelectedParameter("Jump method",
				default_crit, linear_crit, exponential_crit, parabola_crit);
	}

	/**
	 * Returns the algorithm's evaluator parameters.
	 */
	@Override
	protected Parameter[] getEvaluatorParameters() {
		return new Parameter[] { distanceMeasure, kernel, computation, numberOfEigenvectors, kernelParameter };
	}

	/**
	 * Creates a new instances of the evalutor used by this algorithm.
	 */
	@Override
	public ASEvaluation createEvaluator(Classifier classifier, PipelineData data) {		
		
		DISTANCE_MEASURE distanceMeasureType = null;
		SelectedParameter selectedParameter = (SelectedParameter) distanceMeasure.getValue();
		SelectedParameterItem item = selectedParameter.getItems().get(selectedParameter.getSelectedIndex()); 
		
		if (bhattacharyya.equals(item)) {
			distanceMeasureType = DISTANCE_MEASURE.BHATTACHARYYA;
		} else if (divergence.equals(item)) {
			distanceMeasureType = DISTANCE_MEASURE.KL_DIVERGENCE;
		} else if (betweenClassScatterMatrix.equals(item)) {
			distanceMeasureType = DISTANCE_MEASURE.BETWEEN_CLASS_SCATTER_MATRIX;
		} else {
			return null;
		}

		selectedParameter = (SelectedParameter) kernel.getValue();
		item = selectedParameter.getItems().get(selectedParameter.getSelectedIndex());
		if (rbfKernel.equals(item)) {
			return new ReproducingKernelHilbertSpaceEvaluator(KERNEL.RBF, distanceMeasureType, (Integer) numberOfEigenvectors.getValue(),
					(Double) kernelParameter.getValue());
		} else if (polynomialKernel.equals(item)) {
			return new ReproducingKernelHilbertSpaceEvaluator(KERNEL.POLYNOMIAL, distanceMeasureType, (Integer) numberOfEigenvectors.getValue(),
					(Double) kernelParameter.getValue());
		} else if (linearKernel.equals(item)) {
			return new ProbabilisticDistanceEvaluator(distanceMeasureType);
		}
		return null;
	}

	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		if (evaluator instanceof ReproducingKernelHilbertSpaceEvaluator) {
			
			return "Final bound: " + ((BranchAndBound) search).getFinalBound() + "\nSearch time: " + ((BranchAndBound) search).getExecutionTime()
					+ " ms\nNumber of evaluations: " + ((ReproducingKernelHilbertSpaceEvaluator) evaluator).getNumberOfEvaluations();
		} else {
			return "Final bound: " + ((BranchAndBound) search).getFinalBound() + "\nSearch time: " + ((BranchAndBound) search).getExecutionTime();
		}

	}
}
