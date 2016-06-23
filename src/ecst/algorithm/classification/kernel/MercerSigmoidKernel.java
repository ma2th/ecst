package ecst.algorithm.classification.kernel;

import org.apache.commons.math3.util.FastMath;

import weka.classifiers.functions.supportVector.Kernel;
import weka.core.Instance;
import weka.core.Utils;

/**
 * Implementation according to: Carrington et al., A New Mercer Sigmoid Kernel for Clinical Data Classification, EMBC 2014.
 * 
 * @author Matthias Ring
 * 
 */
public class MercerSigmoidKernel extends Kernel {

	private static final long serialVersionUID = 1L;

	private double b;
	private double d;
	private int evaluations;
	private int cacheHits;
	private double[][] kernelMatrix;

	public MercerSigmoidKernel() {
		super();

		this.evaluations = 0;
	}

	@Override
	public String globalInfo() {
		return "Implementation according to: Carrington et al., A New Mercer Sigmoid Kernel for Clinical Data Classification, EMBC 2014.";
	}

	@Override
	public double eval(int id1, int id2, Instance useIfID1isMinus1) throws Exception {
		// cache everything...
		if (kernelMatrix == null) {
			kernelMatrix = new double[m_data.numInstances()][];
			for (int i = 0; i < m_data.numInstances(); i++) {
				kernelMatrix[i] = new double[i + 1];
				for (int j = 0; j <= i; j++) {
					evaluations++;
					kernelMatrix[i][j] = computeKernel(m_data.instance(i), m_data.instance(j));
				}
			}
		}

		if (id1 != -1) {
			cacheHits++;
			if (id1 > id2) {
				return kernelMatrix[id1][id2];
			} else {
				return kernelMatrix[id2][id1];
			}
		} else {
			evaluations++;
			return computeKernel(useIfID1isMinus1, m_data.instance(id2));
		}
	}

	private double computeKernel(Instance instance1, Instance instance2) {
		double result = 0.0;
		double p = instance1.numAttributes() - 1;

		for (int i = 0; i < p; i++) {
			result += FastMath.tanh((instance1.value(i) - d) / b) * FastMath.tanh((instance2.value(i) - d) / b);
		}		
		result = result / p;

		return result;
	}

	public void setOptions(String[] options) throws Exception {
		String tmp;

		tmp = Utils.getOption('D', options);
		if (!"".equals(tmp)) {
			this.d = Double.parseDouble(tmp);
		}
		tmp = Utils.getOption('B', options);
		if (!"".equals(tmp)) {
			this.b = Double.parseDouble(tmp);
		}

	}

	public String[] getOptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void clean() {
		kernelMatrix = null;
		evaluations = 0;
		cacheHits = 0;
	}

	@Override
	public int numEvals() {
		return evaluations;
	}

	@Override
	public int numCacheHits() {
		return cacheHits;
	}

	public double getB() {
		return b;
	}

	public void setB(double b) {
		this.b = b;
	}

	public double getD() {
		return d;
	}

	public void setD(double d) {
		this.d = d;
	}

}
