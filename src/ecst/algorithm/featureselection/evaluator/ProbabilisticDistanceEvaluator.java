package ecst.algorithm.featureselection.evaluator;

import java.util.BitSet;

import org.apache.commons.math3.util.FastMath;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.SubsetEvaluator;
import weka.core.Instances;
import weka.core.matrix.Matrix;
import ecst.algorithm.featureselection.BranchAndBoundAdapter.DISTANCE_MEASURE;
import ecst.utilities.InstanceUtilities;
import ecst.utilities.MathUtilities;

/**
 * This function evaluates subsets in feature selection algorithms by the
 * Bhattacharyya distance. It is used in the branch-and-bound search.
 * 
 * @author Matthias Ring
 * 
 */
public class ProbabilisticDistanceEvaluator extends ASEvaluation implements SubsetEvaluator {

	private static final long serialVersionUID = 1L;

	private DISTANCE_MEASURE distanceMeasure;

	private Matrix mDiff;
	private Matrix cov;
	private Matrix cov1;
	private Matrix cov2;

	public ProbabilisticDistanceEvaluator(DISTANCE_MEASURE distanceMeasure) {
		if (distanceMeasure == DISTANCE_MEASURE.BETWEEN_CLASS_SCATTER_MATRIX) {
			throw new IllegalArgumentException("Not yet implemented!");
		}
		this.distanceMeasure = distanceMeasure;	
	}

	/**
	 * Builds a new evaluator for the given data.
	 */
	@Override
	public void buildEvaluator(Instances data) throws Exception {
		double[] m1;
		double[] m2;
		double[] diff;
		Instances instances1 = null;
		Instances instances2 = null;
		Instances[] separatedInstances = null;

		if (!(data.classAttribute().numValues() == 2 && data.classAttribute().isNominal())) {
			throw new IllegalArgumentException("Only two class problems with nominal class attributes are supported");
		}

		separatedInstances = InstanceUtilities.separateClasses(data);
		instances1 = separatedInstances[0];
		instances2 = separatedInstances[1];

		m1 = new double[instances1.numAttributes() - 1];
		m2 = new double[instances2.numAttributes() - 1];
		diff = new double[instances1.numAttributes() - 1];
		for (int i = 0; i < m1.length; i++) {
			m1[i] = instances1.meanOrMode(i);
			m2[i] = instances2.meanOrMode(i);
			diff[i] = m1[i] - m2[i];
		}
		mDiff = new Matrix(diff, diff.length);

		cov1 = MathUtilities.covarianceMatrix(m1, instances1);
		cov2 = MathUtilities.covarianceMatrix(m2, instances2);
		cov = cov1.plus(cov2);
		cov = cov.times(0.5);
	}

	/**
	 * Evaluates a feature subset.
	 */
	@Override
	public double evaluateSubset(BitSet subset) throws Exception {
		int nextRow = 0;
		int nextColumn = 0;
		double detCov;
		double detCov1;
		double detCov2;
		double distance;
		Matrix mSubset = null;
		Matrix covSubset = null;
		Matrix cov1Subset = null;
		Matrix cov2Subset = null;
		double[] mArray = new double[subset.cardinality()];
		double[][] covArray = new double[subset.cardinality()][subset.cardinality()];
		double[][] cov1Array = new double[subset.cardinality()][subset.cardinality()];
		double[][] cov2Array = new double[subset.cardinality()][subset.cardinality()];

		// ////////////////////////////////////////////////////////////
		// start of common base for Chernoff distance and KL divergence
		// ////////////////////////////////////////////////////////////

		if (subset.cardinality() == 0) return Double.NEGATIVE_INFINITY;

		for (int i = subset.nextSetBit(0); i >= 0; i = subset.nextSetBit(i + 1)) {
			mArray[nextRow] = mDiff.get(i, 0);
			nextRow++;
		}
		mSubset = new Matrix(mArray, mArray.length);

		nextRow = 0;
		for (int i = 0; i < covArray[0].length; i++) {
			nextRow = subset.nextSetBit(nextRow);
			nextColumn = 0;
			for (int j = 0; j < covArray[0].length; j++) {
				nextColumn = subset.nextSetBit(nextColumn);
				covArray[i][j] = cov.get(nextRow, nextColumn);
				cov1Array[i][j] = cov1.get(nextRow, nextColumn);
				cov2Array[i][j] = cov2.get(nextRow, nextColumn);
				nextColumn++;
			}
			nextRow++;
		}
		covSubset = new Matrix(covArray);
		cov1Subset = new Matrix(cov1Array);
		cov2Subset = new Matrix(cov2Array);

		detCov = covSubset.det();
		detCov1 = cov1Subset.det();
		detCov2 = cov2Subset.det();

		// //////////////////////////////////////////////////////////
		// end of common base for Chernoff distance and KL divergence
		// //////////////////////////////////////////////////////////
		
		if (DISTANCE_MEASURE.BHATTACHARYYA.equals(distanceMeasure)) {
			if (detCov != 0.0) {
				covSubset = covSubset.inverse();
				distance = (1.0 / 8.0) * mSubset.transpose().times(covSubset).times(mSubset).get(0, 0);
				distance += (0.5 * FastMath.log(FastMath.E, (detCov) / (FastMath.sqrt(detCov1 * detCov2))));

				return distance;
			}
		} else if (DISTANCE_MEASURE.KL_DIVERGENCE.equals(distanceMeasure)) {
			if (detCov2 != 0.0) {
				cov2Subset = cov2Subset.inverse();

				distance = 0.5 * mSubset.transpose().times(cov2Subset).times(mSubset).get(0, 0);
				distance += 0.5 * FastMath.log(FastMath.E, detCov2 / detCov1);
				distance += 0.5 * cov1Subset.times(cov2Subset).minus(MathUtilities.eyeMatrix(mSubset.getRowDimension())).trace();

				return distance;
			}
		}
		System.err.println("Covariance matrix is singular for subset: " + subset);
		return Double.NaN;
	}
}
