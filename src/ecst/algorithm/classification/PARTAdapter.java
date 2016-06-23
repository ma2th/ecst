package ecst.algorithm.classification;

import java.lang.reflect.Field;
import java.util.Vector;

import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import weka.classifiers.Classifier;
import weka.classifiers.rules.PART;
import weka.classifiers.rules.part.ClassifierDecList;
import weka.classifiers.rules.part.MakeDecList;

/**
 * This class is a adapter for the WEKA implementation of the PART classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class PARTAdapter extends ClassificationAlgorithm {

	private Parameter pruningThreshold;
	private Parameter minObjectsPerLeaf;
	private Parameter useReducedErrorPruning;
	private Parameter foldsReducedErrorPruning;
	private Parameter useOnlyBinarySplits;
	private Parameter unprunedDecisionList;
	private Parameter seed;
	private int numberOfRules;
	private int comparisons;
	@SuppressWarnings("rawtypes")
	private Vector theRules;

	/**
	 * Initializes the algorithm parameters.
	 */
	@SuppressWarnings("rawtypes")
	@Override
	protected void initClassifierParameters() {
		pruningThreshold = new Parameter(0.25, "Confidence threshold for pruning", Parameter.TYPE.DOUBLE, "-C");
		minObjectsPerLeaf = new Parameter(2, "Minimum number of objects per leaf", Parameter.TYPE.INTEGER, "-M");
		useReducedErrorPruning = new Parameter(false, "Reduced error pruning", Parameter.TYPE.BOOLEAN, "-R");
		foldsReducedErrorPruning = new Parameter(null, "Number of folds for reduced error pruning", Parameter.TYPE.INTEGER, "-N");
		useOnlyBinarySplits = new Parameter(false, "Use only binary splits", Parameter.TYPE.BOOLEAN, "-B");
		unprunedDecisionList = new Parameter(false, "Generate unpruned decision list", Parameter.TYPE.BOOLEAN, "-U");
		seed = new Parameter(1, "Seed for random data shuffling", Parameter.TYPE.INTEGER, "-Q");
		theRules = new Vector();
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { pruningThreshold, minObjectsPerLeaf, useOnlyBinarySplits, useReducedErrorPruning,
				foldsReducedErrorPruning, unprunedDecisionList, seed };
	}

	/**
	 * Returns the grid search parameters of this algorithm.
	 */
	@Override
	protected Parameter[] getGridSearchParameters() {
		return null;
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return PART.class;
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
		comparisons = getNumberOfComparisons((PART) classifier);
		numberOfRules = (int) ((PART) classifier).measureNumRules();
	}

	/**
	 * No dependencies for this algoritm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * Sets the factors of the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if ("comparisons".equals(multiplier.getName())) {
			multiplier.setFactor(comparisons);
		} else if ("rules".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfRules);
		}
	}

	/**
	 * Internal method to perform the complexity analysis. Returns the number of
	 * comparisons in the worst case.
	 * 
	 * @param nodesPerLayer
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private int getNumberOfComparisons(PART part) throws Exception {
		int index;
		Field field = null;
		int comparisons = 0;
		MakeDecList m_root = null;
		ClassifierDecList rule = null;
		ClassifierDecList[] children = null;

		field = PART.class.getDeclaredField("m_root");
		field.setAccessible(true);
		m_root = (MakeDecList) field.get(part);

		field = MakeDecList.class.getDeclaredField("theRules");
		field.setAccessible(true);
		theRules = (Vector) field.get(m_root);

		for (int i = 0; i < theRules.size(); i++) {
			rule = (ClassifierDecList) theRules.get(i);
			field = ClassifierDecList.class.getDeclaredField("m_sons");
			field.setAccessible(true);
			children = (ClassifierDecList[]) field.get(rule);

			field = ClassifierDecList.class.getDeclaredField("indeX");
			field.setAccessible(true);
			index = (int) field.getInt(rule);

			if (children != null) {
				comparisons++;
				comparisons += sumRecursively(children[index]);
			}
		}

		return comparisons;
	}

	/**
	 * Internal method to perform the complexity analysis. Sums the number of
	 * comparisons recursively.
	 * 
	 * @param nodesPerLayer
	 * @return
	 */
	private int sumRecursively(ClassifierDecList node) throws Exception {
		int index;
		boolean leaf;
		Field field = null;
		ClassifierDecList[] children = null;

		field = ClassifierDecList.class.getDeclaredField("m_isLeaf");
		field.setAccessible(true);
		leaf = field.getBoolean(node);

		if (leaf) {
			return 0;
		} else {
			field = ClassifierDecList.class.getDeclaredField("m_sons");
			field.setAccessible(true);
			children = (ClassifierDecList[]) field.get(node);

			field = ClassifierDecList.class.getDeclaredField("indeX");
			field.setAccessible(true);
			index = (int) field.getInt(node);

			return sumRecursively(children[index]) + 1;
		}
	}

	@Override
	public String modelToXML(PipelineData data) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < numberOfRules; i++){
			builder.append("<decision rule=\"");
			String s = theRules.get(i).toString();
			for(int l = 0; l < s.length(); l++)
				if(s.charAt(l) == '<')
					builder.append("&lt;");
				else if(s.charAt(l) == '>')
					builder.append("&gt;");
				else
					builder.append(s.charAt(l));
			builder.append("\"/>\n");
		}
		return builder.toString();
	}
}
