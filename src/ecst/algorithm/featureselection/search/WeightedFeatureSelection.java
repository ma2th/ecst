package ecst.algorithm.featureselection.search;

import java.math.BigInteger;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import ecst.utilities.CommonUtilities;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.SubsetEvaluator;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * This class implements the search method for the weighted feature selection.
 * 
 * @author Matthias Ring
 * 
 */
public class WeightedFeatureSelection extends ASSearch implements OptionHandler {

	private static final long serialVersionUID = 1L;

	private enum SEARCH_METHOD {
		EXHAUSTIVE, SEQUENTIAL_FORWARD_SELECTION, SEQUENTIAL_BACKWARD_SELECTION
	};

	private SEARCH_METHOD searchMethod;
	private int[] weights;
	private double minWeight;
	private double maxWeight;
	private double bestMerit;
	private BitSet bestBitSet;

	/**
	 * Constructor.
	 */
	public WeightedFeatureSelection() {
		minWeight = 0.0;
		maxWeight = Double.POSITIVE_INFINITY;
		bestMerit = Double.NEGATIVE_INFINITY;
		searchMethod = SEARCH_METHOD.EXHAUSTIVE;
	}

	/**
	 * Returns a string describing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Feature selection with weights for each feature. "
				+ "The total weight of the selected features has to be between "
				+ "the given minimum and maximum weight. The algorithm stops adding/removing features " +
				"if the evaluation criterion starst decreasing " +
				"even if the minimum or maximum weight is not yet reached. Note that this is not a WEKA class.";
	}

	/**
	 * WEKA method, not implemented.
	 */
	@Override
	public String[] getOptions() {
		return null;
	}

	/**
	 * WEKA method, not implemented.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration listOptions() {
		return null;
	}

	/**
	 * Parses the WEKA options string.
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		String lowerString = null;
		String upperString = null;
		String weightsString = null;
		StringTokenizer tokenzier = null;
		List<Integer> weightsList = new LinkedList<Integer>();

		lowerString = Utils.getOption('L', options);
		upperString = Utils.getOption('U', options);
		weightsString = Utils.getOption('W', options);
		if (!"".equals(lowerString)) {
			minWeight = Double.parseDouble(lowerString);
		}
		if (!"".equals(upperString)) {
			maxWeight = Double.parseDouble(upperString);
		}
		if (!"".equals(weightsString)) {
			tokenzier = new StringTokenizer(weightsString, ",");
			while (tokenzier.hasMoreTokens()) {
				weightsList.add(Integer.parseInt(tokenzier.nextToken().trim()));
			}
		}
		weights = new int[weightsList.size()];
		for (int i = 0; i < weightsList.size(); i++) {
			weights[i] = weightsList.get(i);
		}

		if (Utils.getFlag("E", options)) {
			searchMethod = SEARCH_METHOD.EXHAUSTIVE;
		} else if (Utils.getFlag("F", options)) {
			searchMethod = SEARCH_METHOD.SEQUENTIAL_FORWARD_SELECTION;
		} else if (Utils.getFlag("B", options)) {
			searchMethod = SEARCH_METHOD.SEQUENTIAL_BACKWARD_SELECTION;
		}
	}

	/**
	 * Search the best feature subset with the user defined method.
	 */
	@Override
	public int[] search(ASEvaluation ASEvaluator, Instances instances) throws Exception {
		SubsetEvaluator evaluator = (SubsetEvaluator) ASEvaluator;

		initWeights(instances);
		if (searchMethod.equals(SEARCH_METHOD.SEQUENTIAL_FORWARD_SELECTION)) {
			return forwardSelection(evaluator, instances);
		} else if (searchMethod.equals(SEARCH_METHOD.SEQUENTIAL_BACKWARD_SELECTION)) {
			return backwardSelection(evaluator, instances);
		}
		return exhaustiveSearch(evaluator, instances);
	}

	/**
	 * Performs an exhaustive search with respect to the feature weights.
	 * Evaluates only if lower <= weight <= upper
	 */
	private int[] exhaustiveSearch(SubsetEvaluator evaluator, Instances instances) throws Exception {
		BitSet bitSet = null;
		BigInteger upperEndPlusOne = null;
		BigInteger counter = BigInteger.ONE;
		int numAttributes = instances.numAttributes() - 1;

		upperEndPlusOne = (new BigInteger("2")).pow(numAttributes).add(BigInteger.ONE);
		do {
			bitSet = CommonUtilities.bigIntegerToBitset(counter, numAttributes);
			if (checkBounds(bitSet, numAttributes)) {
				setBestSubset(evaluator.evaluateSubset(bitSet), bitSet, numAttributes);
			}
			counter = counter.add(BigInteger.ONE);
		} while (!counter.equals(upperEndPlusOne));

		return getBestIntArray(numAttributes);
	}

	/**
	 * Performs a forward selection with respect to the feature weights.
	 * 
	 * @param evaluator
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	private int[] forwardSelection(SubsetEvaluator evaluator, Instances instances) throws Exception {
		double merit;
		int maxPosition;
		BitSet bitSet = null;
		double currentBestMerit;
		int numAttributes = instances.numAttributes() - 1;

		bitSet = new BitSet(numAttributes + 1);
		for (int i = 0; i < numAttributes && checkUpperBound(bitSet, numAttributes); i++) {
			maxPosition = -1;
			currentBestMerit = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < numAttributes; j++) {
				if (!bitSet.get(j)) {
					bitSet.set(j);
					if (checkUpperBound(bitSet, numAttributes)) {
						merit = evaluator.evaluateSubset(bitSet);
						if (merit > currentBestMerit) {
							currentBestMerit = merit;
							maxPosition = j;
						}
					}
					bitSet.clear(j);
				}
			}
			if (maxPosition == -1) {
				break;
			} else {
				bitSet.set(maxPosition);
				setBestSubset(currentBestMerit, bitSet, numAttributes);
			}
		}

		return getBestIntArray(numAttributes);
	}

	/**
	 * Performs a backward selection with respect to the feature weights.
	 * 
	 * @param evaluator
	 * @param instances
	 * @return
	 * @throws Exception
	 */
	private int[] backwardSelection(SubsetEvaluator evaluator, Instances instances) throws Exception {
		double merit;
		int maxPosition;
		BitSet bitSet = null;
		double currentBestMerit;
		int numAttributes = instances.numAttributes() - 1;

		bitSet = new BitSet(numAttributes + 1);
		bitSet.set(0, numAttributes);
		for (int i = 0; i < numAttributes && checkLowerBound(bitSet, numAttributes); i++) {
			maxPosition = -1;
			currentBestMerit = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < numAttributes; j++) {
				if (bitSet.get(j)) {
					bitSet.clear(j);
					if (checkLowerBound(bitSet, numAttributes)) {
						merit = evaluator.evaluateSubset(bitSet);
						if (merit > currentBestMerit) {
							currentBestMerit = merit;
							maxPosition = j;
						}
					}
					bitSet.set(j);
				}
			}
			if (maxPosition == -1) {
				break;
			} else {
				bitSet.clear(maxPosition);
				setBestSubset(currentBestMerit, bitSet, numAttributes);
			}
		}

		return getBestIntArray(numAttributes);
	}

	/**
	 * Initalizes the weights for every feature.
	 * 
	 * @param instances
	 */
	private void initWeights(Instances instances) {
		int[] weightsTmp = new int[instances.numAttributes() - 1];

		for (int i = 0; i < weightsTmp.length; i++) {
			weightsTmp[i] = 1;
		}
		for (int i = 0; i < weights.length; i++) {
			weightsTmp[i] = weights[i];
		}
		weights = weightsTmp;
	}

	/**
	 * Returns the result in form of an integer array.
	 * 
	 * @param numAttributes
	 * @return
	 */
	private int[] getBestIntArray(int numAttributes) {
		if (bestBitSet == null) {
			bestBitSet = new BitSet(numAttributes);
		}
		return CommonUtilities.bitsetToIntegerArray(bestBitSet, false);
	}

	/**
	 * Sets the new best subset if the weight constraints are fulfilled.
	 * 
	 * @param merit
	 * @param bitSet
	 * @param numAttributes
	 */
	private void setBestSubset(double merit, BitSet bitSet, int numAttributes) {
		if (merit > bestMerit && checkBounds(bitSet, numAttributes)) {
			bestMerit = merit;
			bestBitSet = (BitSet) bitSet.clone();
		}
	}

	/**
	 * Checks if the lower weight bound is fulfilled.
	 * 
	 * @param bitSet
	 * @param numAttributes
	 * @return
	 */
	private boolean checkLowerBound(BitSet bitSet, int numAttributes) {
		if (minWeight <= sumWeight(bitSet, numAttributes)) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the upper weight bound is fulfilled.
	 * 
	 * @param bitSet
	 * @param numAttributes
	 * @return
	 */
	private boolean checkUpperBound(BitSet bitSet, int numAttributes) {
		if (sumWeight(bitSet, numAttributes) <= maxWeight) {
			return true;
		}
		return false;
	}

	/**
	 * Checks if the lower and upper weight bound is fulfilled.
	 * 
	 * @param bitSet
	 * @param numAttributes
	 * @return
	 */
	private boolean checkBounds(BitSet bitSet, int numAttributes) {
		if (checkLowerBound(bitSet, numAttributes) && checkUpperBound(bitSet, numAttributes)) {
			return true;
		}
		return false;
	}

	/**
	 * Sums the weights of the given subset.
	 * 
	 * @param bitSet
	 * @param numAttributes
	 * @return
	 */
	private int sumWeight(BitSet bitSet, int numAttributes) {
		int weigth = 0;

		for (int i = 0; i < numAttributes; i++) {
			if (bitSet.get(i)) {
				weigth += weights[i];
			}
		}
		return weigth;
	}

}
