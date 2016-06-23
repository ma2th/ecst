package ecst.utilities;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

import weka.classifiers.functions.supportVector.CachedKernel;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.matrix.Matrix;

/**
 * This class contains various mathematical methods.
 * 
 * @author Matthias Ring
 * 
 */
public class MathUtilities {

	/**
	 * Sums the integer array up.
	 * 
	 * @param array
	 * @return
	 */
	public static int sumIntArray(int[] array) {
		int sum = 0;

		for (int i = 0; i < array.length; i++) {
			sum += array[i];
		}
		return sum;
	}

	/**
	 * Creates a SummaryStatistics object for the given data.
	 * 
	 * @param data
	 * @return
	 */
	public static SummaryStatistics createSummaryStatistics(double[] data) {
		SummaryStatistics statistics = new SummaryStatistics();

		for (int i = 0; i < data.length; i++) {
			statistics.addValue(data[i]);
		}
		return statistics;
	}

	/**
	 * Creates a DescriptiveStatistics object for the given data.
	 * 
	 * @param data
	 * @return
	 */
	public static DescriptiveStatistics createDescriptiveStatistics(double[] data) {
		DescriptiveStatistics statistics = new DescriptiveStatistics();

		for (int i = 0; i < data.length; i++) {
			statistics.addValue(data[i]);
		}
		return statistics;
	}

	/**
	 * Returns the gauss sum.
	 * 
	 * @param n
	 * @return
	 */
	public static int gaussSum(int n) {
		return (n * (n + 1)) / 2;
	}

	/**
	 * Computes the energy of the given data.
	 * 
	 * @param data
	 * @return
	 */
	public static double energy(double[] data) {
		double energy = 0.0;

		for (int i = 0; i < data.length; i++) {
			energy += Math.abs(data[i]);
		}

		return energy;
	}

	/**
	 * Determines if the number is a prime.
	 * 
	 * @param number
	 * @return
	 */
	public static boolean isPrime(int number) {
		for (int i = 2; i < number; i++) {
			if (number % i == 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Computes primes.
	 * 
	 * @param count
	 * @return
	 */
	public static int[] computePrimes(int count) {
		int prime = 2;
		int[] primes = new int[count];

		for (int i = 0; i < count; i++) {
			primes[i] = prime;
			do {
				prime++;
			} while (!isPrime(prime));
		}

		return primes;
	}

	/**
	 * Computes the mean of the given list.
	 * 
	 * @param list
	 * @return
	 */
	public static double mean(List<Double> list) {
		double mean = 0.0;

		for (int i = 0; i < list.size(); i++) {
			mean += list.get(i);
		}

		return mean / (double) list.size();
	}

	/**
	 * Computes the covariance matrix for the given instances.
	 * 
	 * @param mean
	 * @param instances
	 * @return
	 */
	public static Matrix covarianceMatrix(double[] mean, Instances instances) {
		Matrix instanceVector = null;
		Matrix differenceVector = null;
		Matrix transposedVector = null;
		Matrix product = null;
		Matrix meanMatrix = new Matrix(mean, mean.length);
		Matrix covarianceMatrix = new Matrix(mean.length, mean.length);

		for (int i = 0; i < instances.numInstances(); i++) {
			double[] tmp = new double[instances.numAttributes() - 1];
			System.arraycopy(instances.instance(i).toDoubleArray(), 0, tmp, 0, tmp.length);
			instanceVector = new Matrix(tmp, tmp.length);
			differenceVector = instanceVector.minus(meanMatrix);
			transposedVector = differenceVector.transpose();
			product = differenceVector.times(transposedVector);
			covarianceMatrix = covarianceMatrix.plus(product);
		}
		covarianceMatrix = covarianceMatrix.times(1.0 / (instances.numInstances() - 1.0));

		return covarianceMatrix;
	}

	/**
	 * Computes the Gram matrix. Requires the instances to be sorted according
	 * to their class membership!!!
	 * 
	 * @return
	 * @throws Exception
	 */
	public static Matrix gramMatrix(int sizeClassOne, int sizeClassTwo, CachedKernel kernel) throws Exception {
		int n = 0;
		double dotproduct = Double.NaN;
		Matrix gramMatrix = null;

		n = sizeClassOne + sizeClassTwo;
		gramMatrix = new Matrix(n, n);

		// Upper left part of gram matrix - only class one
		for (int i = 0; i < sizeClassOne; i++) {
			for (int j = i; j < sizeClassOne; j++) {
				dotproduct = kernel.eval(i, j, null);
				// upper left matrix is symmetric
				gramMatrix.set(i, j, dotproduct);
				gramMatrix.set(j, i, dotproduct);
			}
		}
		// Lower right part of gram matrix - only class two
		for (int i = sizeClassOne; i < (sizeClassOne + sizeClassTwo); i++) {
			for (int j = i; j < (sizeClassOne + sizeClassTwo); j++) {
				dotproduct = kernel.eval(i, j, null);
				// lower right matrix is symmetric
				gramMatrix.set(i, j, dotproduct);
				gramMatrix.set(j, i, dotproduct);
			}
		}
		// Lower left and upper right part of gram matrix - both classes
		for (int classOne = 0, i = 0; classOne < sizeClassOne; classOne++, i++) {
			for (int classTwo = sizeClassOne, j = sizeClassOne; classTwo < (sizeClassOne + sizeClassTwo); classTwo++, j++) {
				dotproduct = kernel.eval(classOne, classTwo, null);
				// complete gram matrix is symmetric
				gramMatrix.set(i, j, dotproduct);
				gramMatrix.set(j, i, dotproduct);
			}
		}

		return gramMatrix;
	}

	public static Matrix onesMatrix(int rows, int columns) {
		return new Matrix(rows, columns, 1.0);
	}

	public static Matrix zeroMatrix(int rows, int columns) {
		return new Matrix(rows, columns, 0.0);
	}

	public static Matrix eyeMatrix(int size) {
		return eyeMatrix(size, size);
	}

	public static Matrix eyeMatrix(int rows, int columns) {
		Matrix matrix = new Matrix(rows, columns, 0.0);

		for (int i = 0; i < (rows < columns ? rows : columns); i++) {
			matrix.set(i, i, 1.0);
		}

		return matrix;
	}

	/**
	 * If the argument is a vector, then a diagonal matrix with the vector
	 * elements will be returned. If the argument is a matrix, then the diagonal
	 * elements in vector form will be returned.
	 * 
	 * @param m
	 * @return
	 */
	public static Matrix diagMatrix(Matrix matrix) {
		Matrix m = null;
		int rows = matrix.getRowDimension();
		int columns = matrix.getColumnDimension();

		if (rows == 1 && columns > 1) {
			m = new Matrix(columns, columns, 0.0);
			for (int i = 0; i < columns; i++) {
				m.set(i, i, matrix.get(0, i));
			}
		} else if (rows > 1 && columns == 1) {
			m = new Matrix(rows, rows, 0.0);
			for (int i = 0; i < rows; i++) {
				m.set(i, i, matrix.get(i, 0));
			}
		} else if (rows > 1 && columns > 1) {
			m = new Matrix(rows < columns ? rows : columns, 1);
			for (int i = 0; i < (rows < columns ? rows : columns); i++) {
				m.set(i, 0, matrix.get(i, i));
			}
		} else {
			return matrix.copy(); // rows == columns == 1
		}
		return m;
	}

	public static Matrix appendMatricesRowWise(Matrix... matrices) {
		int rows = 0;
		int columns = 0;
		int rowCounter = 0;
		Matrix matrix = null;

		columns = matrices[0].getColumnDimension();
		for (Matrix m : matrices) {
			rows += m.getRowDimension();
			if (m.getColumnDimension() != columns) {
				throw new IllegalArgumentException("Column dimensions are not equal!");
			}
		}

		matrix = new Matrix(rows, columns);
		for (Matrix m : matrices) {
			matrix.setMatrix(rowCounter, rowCounter + m.getRowDimension() - 1, 0, columns - 1, m);
			rowCounter += m.getRowDimension();
		}

		return matrix;
	}

	public static String matrixDimensionsToString(Matrix matrix) {
		return "" + matrix.getRowDimension() + "x" + matrix.getColumnDimension();
	}

	public static String matrixToString(Matrix m) {
		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				builder.append(m.get(i, j) + " ");
			}
			if (i != m.getRowDimension() - 1) {
				builder.append("\n");
			}
		}

		return builder.toString();
	}

	/**
	 * Returns the array idx of Matlab's sort function: [data idx] = sort(data)
	 * 
	 * @param data
	 * @return
	 */
	public static int[] sortAndReturnIndices(final double[] data) {
		int[] ret = new int[data.length];
		Integer[] indices = new Integer[data.length];

		for (int i = 0; i < indices.length; i++) {
			indices[i] = i;
		}

		Arrays.sort(indices, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return -Double.compare(data[o1], data[o2]);
			}
		});

		for (int i = 0; i < ret.length; i++) {
			ret[i] = indices[i];
		}

		return ret;
	}

	/**
	 * Sorts eigenvectors in decreasing order.
	 * 
	 * @return
	 */
	public static Matrix getSortedEigenvectors(Matrix eigenvectors, Matrix eigenvalues) {
		int[] sortingIndices = null;
		Matrix sortedEigenvectors = new Matrix(eigenvectors.getRowDimension(), eigenvectors.getColumnDimension(), 0.0);

		sortingIndices = sortAndReturnIndices(diagMatrix(eigenvalues).getRowPackedCopy());
		for (int j = 0; j < sortedEigenvectors.getColumnDimension(); j++) {
			sortedEigenvectors.setMatrix(0, sortedEigenvectors.getRowDimension() - 1, j, j,
					eigenvectors.getMatrix(0, eigenvectors.getRowDimension() - 1, sortingIndices[j], sortingIndices[j]));
		}

		return sortedEigenvectors;
	}

	/**
	 * Sorts eigenvalues in decreasing order.
	 * 
	 * @param diagonalEigenvalueMatrix
	 * @return
	 */
	public static Matrix getSortedEigenvalues(Matrix diagonalEigenvalueMatrix) {
		Matrix sortedDiagonalEigenvalueMatrix = null;
		Matrix eigenvalueVector = null;
		Double[] eigenvaluesAsObjects = null;
		double[] eigenvaluesAsPrimitives = null;

		eigenvalueVector = diagMatrix(diagonalEigenvalueMatrix);

		eigenvaluesAsPrimitives = eigenvalueVector.getRowPackedCopy();
		eigenvaluesAsObjects = new Double[eigenvaluesAsPrimitives.length];
		for (int i = 0; i < eigenvaluesAsPrimitives.length; i++) {
			eigenvaluesAsObjects[i] = eigenvaluesAsPrimitives[i];
		}

		Arrays.sort(eigenvaluesAsObjects, Collections.reverseOrder());

		sortedDiagonalEigenvalueMatrix = new Matrix(diagonalEigenvalueMatrix.getRowDimension(), diagonalEigenvalueMatrix.getColumnDimension(), 0.0);
		for (int i = 0; i < eigenvaluesAsObjects.length; i++) {
			sortedDiagonalEigenvalueMatrix.set(i, i, eigenvaluesAsObjects[i]);
		}

		return sortedDiagonalEigenvalueMatrix;
	}

	public static double sumAllMatrixElements(Matrix m) {
		double sum = 0.0;

		for (int i = 0; i < m.getRowDimension(); i++) {
			for (int j = 0; j < m.getColumnDimension(); j++) {
				sum += m.get(i, j);
			}
		}

		return sum;
	}

	public static Instance mean(Instances instances) {
		Instance m = new Instance(instances.numAttributes());

		m.setDataset(instances);
		for (int i = 0; i < m.numAttributes() - 1; i++) {
			m.setValue(i, instances.meanOrMode(i));
		}
		m.setValue(m.classIndex(), instances.instance(0).classValue());

		return m;
	}

}
