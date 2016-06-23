package ecst.algorithm.featureselection.evaluator;

import java.util.BitSet;

import ecst.algorithm.featureselection.BranchAndBoundAdapter.DISTANCE_MEASURE;
import ecst.algorithm.featureselection.BranchAndBoundAdapter.KERNEL;
import ecst.utilities.CommonUtilities;
import ecst.utilities.InstanceUtilities;
import ecst.utilities.MathUtilities;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.SubsetEvaluator;
import weka.classifiers.functions.supportVector.CachedKernel;
import weka.classifiers.functions.supportVector.PolyKernel;
import weka.classifiers.functions.supportVector.RBFKernel;
import weka.core.Instances;
import weka.core.matrix.EigenvalueDecomposition;
import weka.core.matrix.Matrix;

/**
 * Implementation according to
 * 
 * S.K. Zhou and R.Chellappa. From sample similarity to ensemble similarity:
 * probabilistic distance measures in reproducing kernel hilbert space. IEEE
 * Transactions on Pattern Analysis and Machine Intelligence, 28(6):917-929,
 * 2006.
 * 
 * @author Matthias Ring
 * 
 */
public class ReproducingKernelHilbertSpaceEvaluator extends ASEvaluation implements SubsetEvaluator {

	private static final long serialVersionUID = 1L;

	private static final double ALPHA_FOR_BHATTACHARYYA_DISTANCE = 0.5;

	private static long numberOfEvaluations;
	private int numEigs;
	private Instances data;
	private double kernelParameter;
	private KERNEL kernelType;
	private DISTANCE_MEASURE distanceMeasure;

	public ReproducingKernelHilbertSpaceEvaluator(KERNEL kernel, DISTANCE_MEASURE distanceMeasure, int numberOfEigenvectors, double kernelParameter) {
		this.kernelType = kernel;
		this.numEigs = numberOfEigenvectors;
		this.distanceMeasure = distanceMeasure;
		this.kernelParameter = kernelParameter;
	}

	@Override
	public void buildEvaluator(Instances data) throws Exception {
		if (!(data.classAttribute().numValues() == 2 && data.classAttribute().isNominal())) {
			throw new IllegalArgumentException("Only two class problems with nominal class attributes are supported");
		}
		this.data = data;
		numberOfEvaluations = 0;
	}

	public long getNumberOfEvaluations() {
		return numberOfEvaluations;
	}

	@Override
	public double evaluateSubset(BitSet subset) throws Exception {
		Matrix gramMatrix = null;
		Instances instances = null;
		int[] subsetIntArray = null;
		CachedKernel cachedKernel = null;
		Instances[] separatedInstances = null;

		numberOfEvaluations++;

		// prepare data for computation of Gram matrix
		subsetIntArray = CommonUtilities.bitsetToIntegerArray(subset, true);
		subsetIntArray[subsetIntArray.length - 1] = data.classIndex();
		instances = InstanceUtilities.attributeSubset(data, subsetIntArray);
		separatedInstances = InstanceUtilities.separateClasses(instances);
		instances = InstanceUtilities.concatenateInstances(separatedInstances[0], separatedInstances[1]);

		if (DISTANCE_MEASURE.BHATTACHARYYA.equals(distanceMeasure) || DISTANCE_MEASURE.KL_DIVERGENCE.equals(distanceMeasure)
				|| DISTANCE_MEASURE.BETWEEN_CLASS_SCATTER_MATRIX.equals(distanceMeasure)) {
			// compute Gram matrix
			if (kernelType.equals(KERNEL.RBF)) {
				cachedKernel = new RBFKernel(instances, 0, kernelParameter);
			} else if (kernelType.equals(KERNEL.POLYNOMIAL)) {
				cachedKernel = new PolyKernel(instances, 0, kernelParameter, true);
			}
			gramMatrix = MathUtilities.gramMatrix(separatedInstances[0].numInstances(), separatedInstances[1].numInstances(), cachedKernel);

			return computeDistance(gramMatrix, separatedInstances[0].numInstances(), separatedInstances[1].numInstances());
		} else {
			return Double.NaN;
		}
	}

	private double computeDistance(Matrix K, int n1, int n2) throws Exception {
		Matrix K11 = null, K12 = null, K21 = null, K22 = null;
		Matrix hK11 = null, hK22 = null;
		Matrix s1 = null, s2 = null;
		Matrix J1 = null, J2 = null;
		Matrix Q1 = null, Q2 = null;
		Matrix V1 = null, V2 = null;
		Matrix JV1 = null, JV2 = null;
		Matrix b11 = null, b12 = null, b21 = null, b22 = null;
		Matrix P = null;
		Matrix L12 = null, L = null;
		Matrix B = null;
		Matrix tmp1 = null;
		Matrix tmp2 = null;
		Matrix A1 = null, B2 = null;
		EigenvalueDecomposition eigs11 = null, eigs22 = null;
		double a11 = Double.NaN, a12 = Double.NaN, a22 = Double.NaN;
		double xi11 = Double.NaN, xi12 = Double.NaN, xi22 = Double.NaN;
		double chernoffDistance = Double.NaN;
		double kl12 = Double.NaN;
		double a21 = Double.NaN;
		double eta12 = Double.NaN;
		double theta121 = Double.NaN, theta222 = Double.NaN, theta122 = Double.NaN, theta221 = Double.NaN;
		double meanDistance = Double.NaN;

		// ////////////////////////////////////////////////////////////
		// start of common base for all distances 
		// ////////////////////////////////////////////////////////////
		
		K11 = K.getMatrix(0, n1 - 1, 0, n1 - 1);
		K12 = K.getMatrix(0, n1 - 1, n1, n1 + n2 - 1);
		K21 = K.getMatrix(n1, n1 + n2 - 1, 0, n1 - 1);
		K22 = K.getMatrix(n1, n1 + n2 - 1, n1, n1 + n2 - 1);

		s1 = MathUtilities.onesMatrix(n1, 1);
		s1 = s1.times(1.0 / ((double) n1));
		s2 = MathUtilities.onesMatrix(n2, 1);
		s2 = s2.times(1.0 / ((double) n2));
		
		a11 = s1.transpose().times(K11).times(s1).get(0, 0);
		a12 = s1.transpose().times(K12).times(s2).get(0, 0);
		a22 = s2.transpose().times(K22).times(s2).get(0, 0);
		
		if (DISTANCE_MEASURE.BETWEEN_CLASS_SCATTER_MATRIX.equals(distanceMeasure)) {
			meanDistance = a11 - 2 * a12 + a22;
			return meanDistance;
		}

		// ////////////////////////////////////////////////////////////
		// start of common base for Chernoff distance and KL divergence
		// ////////////////////////////////////////////////////////////
		
		J1 = MathUtilities.eyeMatrix(n1, n1).minus(s1.times(MathUtilities.onesMatrix(n1, 1).transpose())).times(1.0 / Math.sqrt(n1));
		J2 = MathUtilities.eyeMatrix(n2, n2).minus(s2.times(MathUtilities.onesMatrix(n2, 1).transpose())).times(1.0 / Math.sqrt(n2));

		hK11 = J1.transpose().times(K11).times(J1);
		hK22 = J2.transpose().times(K22).times(J2);

		eigs11 = new EigenvalueDecomposition(hK11);
		eigs22 = new EigenvalueDecomposition(hK22);

		Q1 = eigs11.getV();
		V1 = eigs11.getD();
		Q1 = MathUtilities.getSortedEigenvectors(Q1, V1).getMatrix(0, eigs11.getV().getRowDimension() - 1, 0, numEigs - 1);
		V1 = MathUtilities.getSortedEigenvalues(V1).getMatrix(0, numEigs - 1, 0, numEigs - 1);

		Q2 = eigs22.getV();
		V2 = eigs22.getD();
		Q2 = MathUtilities.getSortedEigenvectors(Q2, V2).getMatrix(0, eigs22.getV().getRowDimension() - 1, 0, numEigs - 1);
		V2 = MathUtilities.getSortedEigenvalues(V2).getMatrix(0, numEigs - 1, 0, numEigs - 1);

		b11 = K11.times(s1);
		b12 = K12.times(s2);
		b21 = K21.times(s1);
		b22 = K22.times(s2);

		JV1 = J1.times(Q1).getMatrix(0, n1 - 1, 0, numEigs - 1);
		JV2 = J2.times(Q2).getMatrix(0, n2 - 1, 0, numEigs - 1);

		// //////////////////////////////////////////////////////////
		// end of common base for Chernoff distance and KL divergence
		// //////////////////////////////////////////////////////////

		if (DISTANCE_MEASURE.BHATTACHARYYA.equals(distanceMeasure)) {
			P = MathUtilities.zeroMatrix(n1 + n2, 2 * numEigs);
			P.setMatrix(0, n1 - 1, 0, numEigs - 1, JV1.times(Math.sqrt(1.0 - ALPHA_FOR_BHATTACHARYYA_DISTANCE)));
			P.setMatrix(n1, n1 + n2 - 1, numEigs, 2 * numEigs - 1, JV2.times(Math.sqrt(ALPHA_FOR_BHATTACHARYYA_DISTANCE)));

			L12 = JV1.transpose().times(K12).times(JV2).times(Math.sqrt(ALPHA_FOR_BHATTACHARYYA_DISTANCE * (1 - ALPHA_FOR_BHATTACHARYYA_DISTANCE)));

			L = MathUtilities.zeroMatrix(2 * numEigs, 2 * numEigs);
			L.setMatrix(0, numEigs - 1, 0, numEigs - 1, V1.times(ALPHA_FOR_BHATTACHARYYA_DISTANCE));
			L.setMatrix(numEigs, 2 * numEigs - 1, numEigs, 2 * numEigs - 1, V2.times(ALPHA_FOR_BHATTACHARYYA_DISTANCE));
			L.setMatrix(0, numEigs - 1, numEigs, 2 * numEigs - 1, L12);
			L.setMatrix(numEigs, 2 * numEigs - 1, 0, numEigs - 1, L12.transpose());

			B = P.times(L.inverse()).times(P.transpose());

			tmp1 = MathUtilities.appendMatricesRowWise(b11, b21);
			xi11 = a11 - tmp1.transpose().times(B).times(tmp1).get(0, 0);

			tmp1 = MathUtilities.appendMatricesRowWise(b11, b21);
			tmp2 = MathUtilities.appendMatricesRowWise(b12, b22);
			xi12 = a12 - tmp1.transpose().times(B).times(tmp2).get(0, 0);

			tmp1 = MathUtilities.appendMatricesRowWise(b12, b22);
			xi22 = a22 - tmp1.transpose().times(B).times(tmp1).get(0, 0);

			// Bhattacharyya distance = Chernoff distance with alpha = 0.5
			chernoffDistance = 0.5 * ALPHA_FOR_BHATTACHARYYA_DISTANCE * (1.0 - ALPHA_FOR_BHATTACHARYYA_DISTANCE) * (xi11 + xi22 - 2.0 * xi12);

			return chernoffDistance;
		} else if (DISTANCE_MEASURE.KL_DIVERGENCE.equals(distanceMeasure)) {
			A1 = JV1.times(JV1.transpose());
			B2 = JV2.times(MathUtilities.diagMatrix(MathUtilities.onesMatrix(numEigs, 1).arrayRightDivide(MathUtilities.diagMatrix(V2))))
					.times(JV2.transpose());

			a21 = a12;

			theta121 = a11 - b21.transpose().times(B2).times(b21).get(0, 0);
			theta222 = a22 - b22.transpose().times(B2).times(b22).get(0, 0);
			theta122 = a12 - b21.transpose().times(B2).times(b22).get(0, 0);
			theta221 = a21 - b22.transpose().times(B2).times(b21).get(0, 0);

			eta12 = A1.times(K12).times(B2).times(K21).trace();

			kl12 = theta121 + theta222 - theta122 - theta221 + V1.trace() - eta12;
			kl12 = 0.5 * kl12;

			return kl12;
		} else {
			return Double.NaN;
		}
	}
}
