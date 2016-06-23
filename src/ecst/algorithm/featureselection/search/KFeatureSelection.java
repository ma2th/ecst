package ecst.algorithm.featureselection.search;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Enumeration;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.SubsetEvaluator;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import ecst.utilities.CommonUtilities;

/**
 * This class implements the sequential search method.
 * 
 * @author Matthias Ring
 * 
 */
public class KFeatureSelection extends ASSearch implements OptionHandler {

	private static final long serialVersionUID = 1L;

	private enum SEARCH_METHOD {
		SEQUENTIAL_FORWARD_SELECTION, SEQUENTIAL_BACKWARD_SELECTION, EXHAUSTIVE
	};

	private SEARCH_METHOD searchMethod;
	private int numberOfFeatures;
	private long executionTime;

	public KFeatureSelection() {
		numberOfFeatures = 1;
		searchMethod = SEARCH_METHOD.SEQUENTIAL_FORWARD_SELECTION;
	}

	public String globalInfo() {
		return "Sequential forward/backward or exhaustive feature selection. The algorithm selects _exact_ as many "
				+ "features as the corresponding parameter defines. Note that this is not a WEKA class.";
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration listOptions() {
		return null; // WEKA-specific method; not implemented.
	}

	@Override
	public String[] getOptions() {
		return null; // WEKA-specific method; not implemented.
	}

	@Override
	public void setOptions(String[] options) throws Exception {
		String numberString = null;

		if (Utils.getFlag("F", options)) {
			searchMethod = SEARCH_METHOD.SEQUENTIAL_FORWARD_SELECTION;
		} else if (Utils.getFlag("B", options)) {
			searchMethod = SEARCH_METHOD.SEQUENTIAL_BACKWARD_SELECTION;
		} else if (Utils.getFlag("E", options)) {
			searchMethod = SEARCH_METHOD.EXHAUSTIVE;
		}

		numberString = Utils.getOption('N', options);
		if (!"".equals(numberString)) {
			numberOfFeatures = Integer.parseInt(numberString);
		}
	}

	@Override
	public int[] search(ASEvaluation ASEvaluator, Instances instances) throws Exception {
		SubsetEvaluator evaluator = (SubsetEvaluator) ASEvaluator;

		if (searchMethod.equals(SEARCH_METHOD.SEQUENTIAL_FORWARD_SELECTION)) {
			return forwardSelection(evaluator, instances);
		} else if (searchMethod.equals(SEARCH_METHOD.SEQUENTIAL_BACKWARD_SELECTION)) {
			return backwardSelection(evaluator, instances);
		} else if (searchMethod.equals(SEARCH_METHOD.EXHAUSTIVE)) {
			return exhaustive(evaluator, instances);
		}
		return null;
	}

	private int[] forwardSelection(SubsetEvaluator evaluator, Instances instances) throws Exception {
		long timeStart;
		long timeEnd;
		double merit;
		int maxPosition;
		BitSet bitSet = null;
		double bestMerit;
		int numAttributes = instances.numAttributes() - 1;

		timeStart = System.currentTimeMillis();
		bitSet = new BitSet(numAttributes + 1);
		for (int i = 0; i < numberOfFeatures; i++) {
			maxPosition = -1;
			bestMerit = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < numAttributes; j++) {
				if (!bitSet.get(j)) {
					bitSet.set(j);
					merit = evaluator.evaluateSubset(bitSet);
					if (merit > bestMerit) {
						bestMerit = merit;
						maxPosition = j;
					}
					bitSet.clear(j);
				}
			}
			bitSet.set(maxPosition);
		}
		timeEnd = System.currentTimeMillis();
		executionTime = timeEnd - timeStart;
		
		return CommonUtilities.bitsetToIntegerArray(bitSet, false);
	}

	private int[] backwardSelection(SubsetEvaluator evaluator, Instances instances) throws Exception {
		long timeStart;
		long timeEnd;
		double merit;
		int maxPosition;
		BitSet bitSet = null;
		double bestMerit;
		int numAttributes = instances.numAttributes() - 1;

		timeStart = System.currentTimeMillis();
		bitSet = new BitSet(numAttributes + 1);
		bitSet.set(0, numAttributes);
		for (int i = 0; i < numAttributes - numberOfFeatures; i++) {
			maxPosition = -1;
			bestMerit = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < numAttributes; j++) {
				if (bitSet.get(j)) {
					bitSet.clear(j);
					merit = evaluator.evaluateSubset(bitSet);
					if (merit > bestMerit) {
						bestMerit = merit;
						maxPosition = j;
					}
					bitSet.set(j);
				}
			}
			bitSet.clear(maxPosition);
		}
		timeEnd = System.currentTimeMillis();
		executionTime = timeEnd - timeStart;
		
		return CommonUtilities.bitsetToIntegerArray(bitSet, false);
	}

	private int[] exhaustive(SubsetEvaluator evaluator, Instances instances) throws Exception {
		long timeStart;
		long timeEnd;
		double merit;
		BitSet bitSet = null;
		BitSet testSet = null;
		BigInteger upperEndPlusOne = null;
		BigInteger counter = BigInteger.ONE;
		double bestMerit = Double.NEGATIVE_INFINITY;
		int numAttributes = instances.numAttributes() - 1;

		timeStart = System.currentTimeMillis();
		upperEndPlusOne = (new BigInteger("2")).pow(numAttributes).add(BigInteger.ONE);
		do {
			testSet = CommonUtilities.bigIntegerToBitset(counter, numAttributes);
			if (testSet.cardinality() == numberOfFeatures) {
				merit = evaluator.evaluateSubset(testSet);
				if (merit > bestMerit) {
					bestMerit = merit;
					bitSet = testSet;
				}
			}
			counter = counter.add(BigInteger.ONE);
		} while (!counter.equals(upperEndPlusOne));
		timeEnd = System.currentTimeMillis();
		executionTime = timeEnd - timeStart;
		
		return CommonUtilities.bitsetToIntegerArray(bitSet, false);
	}
	
	public long getExecutionTime() {
		return executionTime;
	}
}
