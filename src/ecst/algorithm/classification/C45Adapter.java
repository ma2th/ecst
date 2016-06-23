package ecst.algorithm.classification;

import java.lang.reflect.Field;

import weka.classifiers.Classifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.j48.ClassifierTree;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of the C4.5 classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class C45Adapter extends ClassificationAlgorithm {

	private Parameter seed;
	private Parameter useUnprunedTree;
	private Parameter laplaceSmoothing;
	private Parameter noCleanUp;
	private Parameter noSubtreeRaising;
	private Parameter useOnlyBinarySpilts;
	private Parameter useReducedErrorPruning;
	private Parameter foldsForReducedErrorPruning;
	private Parameter minInstancesPerLeaf;
	private Parameter thresholdForPruning;
	private int maxDepth;
	private int treeSize;
	private int leaves;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		useUnprunedTree = new Parameter(false, "Use unpruned tree", Parameter.TYPE.BOOLEAN, "-U");
		seed = new Parameter(1, "Seed for random data shuffling", Parameter.TYPE.INTEGER, "-Q");
		laplaceSmoothing = new Parameter(false, "Laplace smoothing for predicted probabilities", Parameter.TYPE.BOOLEAN, "-A");
		noCleanUp = new Parameter(false, "Do not clean up after the tree has been built", Parameter.TYPE.BOOLEAN, "-L");
		noSubtreeRaising = new Parameter(false, "Do not perform subtree raising", Parameter.TYPE.BOOLEAN, "-S");
		useOnlyBinarySpilts = new Parameter(false, "Use only binary splits", Parameter.TYPE.BOOLEAN, "-B");
		useReducedErrorPruning = new Parameter(false, "Use reduced error pruning", Parameter.TYPE.BOOLEAN, "-R");
		foldsForReducedErrorPruning = new Parameter(null, "Number of folds for reduced error pruning", Parameter.TYPE.INTEGER, "-N");
		minInstancesPerLeaf = new Parameter(2, "Minimum number of instances per leaf", Parameter.TYPE.INTEGER, "-M");
		thresholdForPruning = new Parameter(0.25, "Confidence threshold for pruning", Parameter.TYPE.DOUBLE, "-C");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { seed, useUnprunedTree, laplaceSmoothing, noCleanUp, noSubtreeRaising, useOnlyBinarySpilts,
				useReducedErrorPruning, foldsForReducedErrorPruning, minInstancesPerLeaf, thresholdForPruning };
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
		return J48.class;
	}

	/**
	 * Analyzes the trained classifier to perform the complexity analysis later.
	 */
	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
		Field field = null;
		ClassifierTree rootNode = null;
		J48 c45 = (J48) classifier;

		field = J48.class.getDeclaredField("m_root");
		field.setAccessible(true);
		rootNode = (ClassifierTree) field.get(c45);
		maxDepth = computeMaxDepth(rootNode, 1);
		treeSize = (int) c45.measureTreeSize();
		leaves = (int) c45.measureNumLeaves();
	}

	/**
	 * Internal method to determine the measure necessary for the complexity
	 * analysis: computes the maximum depth of the learned tree.
	 * 
	 * @param rootNode
	 * @param depth
	 * @return
	 * @throws Exception
	 */
	private int computeMaxDepth(ClassifierTree rootNode, int depth) throws Exception {
		Field field = null;
		ClassifierTree[] children = null;
		int currentMax = -1;
		int current = -1;
		boolean isLeaf = false;

		field = ClassifierTree.class.getDeclaredField("m_isLeaf");
		field.setAccessible(true);
		isLeaf = field.getBoolean(rootNode);

		if (isLeaf) {
			return depth;
		}

		field = ClassifierTree.class.getDeclaredField("m_sons");
		field.setAccessible(true);
		children = (ClassifierTree[]) field.get(rootNode);

		for (int i = 0; i < children.length; i++) {
			current = computeMaxDepth(children[i], depth + 1);
			if (current > currentMax) {
				currentMax = current;
			}
		}

		return currentMax;
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
		if ("treeDepthMinusOne".equals(multiplier.getName())) {
			multiplier.setFactor(maxDepth - 1);
		} else if ("innerNodes".equals(multiplier.getName())) {
			multiplier.setFactor((int) (treeSize - leaves));
		} else if ("leaves".equals(multiplier.getName())) {
			multiplier.setFactor((int) leaves);
		} else if ("nodes".equals(multiplier.getName())) {
			multiplier.setFactor((int) treeSize);
		}
	}

	/**
	 * Nothing to postprocess here.
	 */
	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
