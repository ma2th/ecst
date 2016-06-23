package ecst.utilities;

import java.math.BigInteger;
import java.text.NumberFormat;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.Algorithm;
import ecst.combiner.CombinerOutputModel;

/**
 * This class is a collection of various methods used in different classes.
 * 
 * @author Matthias Ring
 * 
 */
public class CommonUtilities {

	/**
	 * Converts a BitSet object to an integer array.
	 * 
	 * @param bitSet
	 * @param plusOneForClass
	 * @return
	 */
	public static int[] bitsetToIntegerArray(BitSet bitSet, boolean plusOneForClass) {
		int size;
		int[] selection;
		int searchStart = 0;

		if (bitSet == null) {
			return null;
		}

		if (plusOneForClass) {
			size = bitSet.cardinality() + 1;
		} else {
			size = bitSet.cardinality();
		}
		selection = new int[size];
		for (int i = 0; i < size; i++) {
			selection[i] = bitSet.nextSetBit(searchStart);
			searchStart = selection[i] + 1;
		}
		return selection;
	}

	/**
	 * Converts an integer lsit into an integer array.
	 * 
	 * @param list
	 * @return
	 */
	public static int[] integerListToArray(List<Integer> list) {
		int[] ret = new int[list.size()];

		for (int i = 0; i < list.size(); i++) {
			ret[i] = list.get(i);
		}
		return ret;
	}

	/**
	 * Converts an algorithm array into an algorithm list.
	 * 
	 * @param algorithms
	 * @return
	 */
	public static List<Algorithm> algorithmArrayToList(Object[] algorithms) {
		List<Algorithm> list = new LinkedList<Algorithm>();
		for (Object algorithm : algorithms) {
			list.add((Algorithm) algorithm);
		}
		return list;
	}

	/**
	 * Converts a BigInteger into a BitSet.
	 * 
	 * @param bigInteger
	 * @param numAttributes
	 * @return
	 */
	public static BitSet bigIntegerToBitset(BigInteger bigInteger, int numAttributes) {
		BitSet bitSet = new BitSet(numAttributes + 1);

		for (int i = 0; i < numAttributes; i++) {
			if (bigInteger.testBit(i)) {
				bitSet.set(i);
			}
		}
		return bitSet;
	}

	/**
	 * Adds an index to the algorithm's name.
	 * 
	 * @param algorithm
	 * @param html
	 * @return
	 */
	public static String createAlgorithmNameWithInstanceCounter(Algorithm algorithm, boolean html) {
		if (algorithm.getInstanceCounter() != null) {
			if (html) {
				return algorithm.getDefinition().getName() + "<font size=\"-1\"><sub>" + algorithm.getInstanceCounter()
						+ "</sub></font>";
			} else {
				return algorithm.getDefinition().getName() + "_" + algorithm.getInstanceCounter();
			}
		} else {
			return algorithm.getDefinition().getName();
		}
	}

	public static String createTPRateString(CombinerOutputModel model, NumberFormat format, boolean html) {
		double mean = 0;
		String result = "";
		String percent = "";

		if (html) {
			percent = "%";
		} else {
			percent = "\\%";
		}

		for (int i = 0; i < model.getData().getFeatureSelectedInstances().numClasses(); i++) {
			mean += model.getEvaluationResult().truePositiveRate(i);
			if (i > 0) {
				result += ", ";
			}
			result += model.getData().getFeatureSelectedInstances().classAttribute().value(i) + ": "
					+ format.format(model.getEvaluationResult().truePositiveRate(i) * 100) + percent;
		}
		mean = mean / model.getData().getFeatureSelectedInstances().numClasses();

		if (html) {
			result = "<html>" + format.format(mean * 100) + percent + " <font size=\"-2\">(" + result
					+ ")</font></html>";
		} else {
			result = format.format(mean * 100) + percent + " (" + result + ")";
		}
		return result;
	}

	/**
	 * Creates a detail string of the complexity analysis that shows which
	 * operations result from which pipeline step.
	 * 
	 * @param sum
	 * @return
	 */
	public static String createComplexityDetailsStringHTML(int[] sum) {
		String result = "";

		result += MathUtilities.sumIntArray(sum);
		if (MathUtilities.sumIntArray(sum) > 0) {
			result += " <font size=\"-2\">";
			result += createComplexityDetailsString(sum);
			result += "</font>";
		}
		return result;
	}

	public static String createComplexityDetailsStringLatex(int[] sum) {
		String result = "";

		result += MathUtilities.sumIntArray(sum);
		if (MathUtilities.sumIntArray(sum) > 0) {
			result += " {\\scriptsize ";
			result += createComplexityDetailsString(sum);
			result += "}";
		}
		return result;
	}

	public static String createComplexityDetailsString(int[] sum) {
		boolean first = true;
		String result = "(";

		if (sum[0] > 0) {
			result += sum[0] + "f";
			first = false;
		}
		if (sum[1] > 0) {
			if (!first) {
				result += ", ";
			} else {
				first = false;
			}
			result += sum[1] + "p";
		}
		if (sum[2] > 0) {
			if (!first) {
				result += ", ";
			}
			result += sum[2] + "c";
		}
		result += ")";
		return result;
	}

	/**
	 * Returns true if the list contains any element of the array.
	 * 
	 * @param list
	 * @param array
	 * @return
	 */
	public static boolean listContainsAnyElement(List<String> list, String[] array) {
		for (String element : array) {
			if (list.contains(element)) {
				return true;
			}
		}
		return false;
	}

}
