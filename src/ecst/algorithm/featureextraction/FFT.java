package ecst.algorithm.featureextraction;

import org.apache.commons.math3.complex.Complex;

import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;
import org.apache.commons.math3.util.FastMath;

import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class implements the FFT feature extraction algorithm.
 * 
 * @author Matthias Ring
 * 
 */
public class FFT extends OneColumnFeatureExtraction {

	private int fftSize;

	/**
	 * Returns the name of the implementing class (= this)
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return FFT.class;
	}

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initFeatureExtractionParameters() {
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	protected Parameter[] getFeatureExtractionParameters() {
		return null;
	}

	/**
	 * Returns the number of features that will be computed. This is the next
	 * power of two of the argument and multiplied by two because of the real
	 * and imaginary part.
	 */
	@Override
	public int getNumberOfFeatures(int linesOfData) {
		fftSize = getNextPowerOfTwo(linesOfData);
		return fftSize * 2;
	}

	private int getNextPowerOfTwo(int length) {
		return (int) FastMath.pow(2, FastMath.ceil(FastMath.log(2, length)));
	}

	/**
	 * Computes the features.
	 */
	@Override
	protected double[] computeFeaturesOnColumn(double[] data) {
		double[] paddedData = null;
		double[] transformedData = null;
		Complex[] transformedComplex = null;
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);

		// padding!
		paddedData = new double[getNextPowerOfTwo(data.length)];
		System.arraycopy(data, 0, paddedData, 0, data.length);
		for (int i = data.length; i < paddedData.length; i++) {
			paddedData[i] = 0.0;
		}

		transformedComplex = fft.transform(paddedData, TransformType.FORWARD);
		transformedData = new double[fftSize * 2];
		for (int i = 0; i < transformedComplex.length; i++) {
			transformedData[i * 2] = transformedComplex[i].getReal();
			transformedData[i * +1] = transformedComplex[i].getImaginary();
		}
		return transformedData;
	}

	/**
	 * No dependencies for this algorithm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * Sets the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if ("numberOfUnitSquareRoots".equals(multiplier.getName())) {
			// partial sum of geometric series
			// multiplier.setFactor((int) (-1.0 * (1.0 - FastMath.pow(2,
			// FastMath.log(2.0, fftSize)))));
			multiplier.setFactor((int) (-1.0 * (1.0 - fftSize)));
		} else if ("numberOfRecursions".equals(multiplier.getName())) {
			// solution of recursion equation: t(n) = 2t(n/2) + 1/2n, t(1) = 0
			multiplier.setFactor((int) (0.5 * fftSize * FastMath.log(2.0, fftSize)));
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
