package ecst.algorithm.classification;

import java.util.StringTokenizer;

import weka.classifiers.Classifier;
import weka.classifiers.functions.MultilayerPerceptron;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter for the WEKA implementation of the Multilayer
 * Perceptron classifier.
 * 
 * @author Matthias Ring
 * 
 */
public class MultilayerPerceptronAdapter extends ClassificationAlgorithm {

	private Parameter learningRate;
	private Parameter momentumRate;
	private Parameter numberOfEpochs;
	private Parameter percentageValidationSet;
	private Parameter seed;
	private Parameter numberOfErrors;
	private Parameter noAutoCreation;
	private Parameter noNominalToBinaryFilter;
	private Parameter noNormalizingClasses;
	private Parameter noResetting;
	private Parameter noNormalizingAttributes;
	private Parameter learningRateDecay;
	private Parameter numbersForNodes;
	private boolean numericClassLabel;
	private int[] nodesPerLayer;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		learningRate = new Parameter(0.3, "Learning rate for backpropagation algorithm", Parameter.TYPE.DOUBLE, "-L");
		momentumRate = new Parameter(0.2, "Momentum rate for backpropagation algorithm", Parameter.TYPE.DOUBLE, "-M");
		numberOfEpochs = new Parameter(500, "Number of training epochs", Parameter.TYPE.INTEGER, "-N");
		percentageValidationSet = new Parameter(0, "Percentage of validation set to terminate training", Parameter.TYPE.INTEGER, "-V");
		seed = new Parameter(0L, "Random number seed", Parameter.TYPE.LONG, "-S");
		numberOfErrors = new Parameter(20, "Number of consecutive errors allowed for validation testing", Parameter.TYPE.INTEGER, "-E");
		noAutoCreation = new Parameter(false, "No automatic creation of network connections", Parameter.TYPE.BOOLEAN, "-A");
		noNominalToBinaryFilter = new Parameter(false, "Use no nominal to binary filter", Parameter.TYPE.BOOLEAN, "-B");
		noNormalizingClasses = new Parameter(false, "No normalizing of numeric classes", Parameter.TYPE.BOOLEAN, "-C");
		noNormalizingAttributes = new Parameter(false, "No normalizing of attributes", Parameter.TYPE.BOOLEAN, "-I");
		noResetting = new Parameter(false, "Resetting the network will not be allowed", Parameter.TYPE.BOOLEAN, "-R");
		learningRateDecay = new Parameter(false, "Learning rate decay will occur", Parameter.TYPE.BOOLEAN, "-D");
		numbersForNodes = new Parameter("a", "Comma seperated numbers for nodes on each layer", Parameter.TYPE.STRING, "-H");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { learningRate, momentumRate, numberOfEpochs, percentageValidationSet, seed, numberOfErrors, noAutoCreation,
				noNominalToBinaryFilter, noNormalizingClasses, noNormalizingAttributes, noResetting, learningRateDecay, numbersForNodes };
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
		return MultilayerPerceptron.class;
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
		nodesPerLayer = nodesPerLayer(((MultilayerPerceptron) classifier).getHiddenLayers(), data.getFeatureSelectedInstances()
				.numAttributes() - 1, data.getFeatureSelectedInstances().numClasses());
		numericClassLabel = data.getFeatureSelectedInstances().classAttribute().isNumeric();
	}

	/**
	 * Adds the data-dependent complexity measures.
	 */
	@Override
	protected void addDependencies() {
		if (numericClassLabel) {
			addDependency("numericClassLabels");
		} else {
			addDependency("nonNumericClassLabels");
		}
	}

	/**
	 * Sets the factors of the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if ("weights".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfWeights(nodesPerLayer));
		} else if ("neurons".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfNeurons(nodesPerLayer));
		} else if ("weightsMinusNeurons".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfWeights(nodesPerLayer) - numberOfNeurons(nodesPerLayer));
		} else if ("neuronsMinusClasses".equals(multiplier.getName())) {
			multiplier.setFactor(numberOfNeurons(nodesPerLayer) - nodesPerLayer[nodesPerLayer.length - 1]);
		}
	}

	/**
	 * Internal method to perform the complexity analysis. Returns the number of
	 * nodes per layer in the neural network.
	 * 
	 * @param hiddenLayersString
	 * @param attributes
	 * @param classes
	 * @return
	 */
	private int[] nodesPerLayer(String hiddenLayersString, int attributes, int classes) {
		int[] nodes = null;
		int counter = 1;
		String hiddenLayer = null;
		StringTokenizer tokenizer = new StringTokenizer(hiddenLayersString, ",");

		nodes = new int[2 + tokenizer.countTokens()];
		nodes[0] = attributes;
		nodes[nodes.length - 1] = classes;
		while (tokenizer.hasMoreTokens()) {
			hiddenLayer = tokenizer.nextToken().trim();
			if ("a".equals(hiddenLayer)) {
				nodes[counter] = (attributes + classes) / 2;
			} else if ("i".equals(hiddenLayer)) {
				nodes[counter] = attributes;
			} else if ("o".equals(hiddenLayer)) {
				nodes[counter] = classes;
			} else if ("t".equals(hiddenLayer)) {
				nodes[counter] = classes + attributes;
			} else {
				nodes[counter] = Integer.parseInt(hiddenLayer);
			}
			counter++;
		}

		return nodes;
	}

	/**
	 * Internal method to perform the complexity analysis. Returns the number of weights in the neural network.
	 * @param nodesPerLayer
	 * @return
	 */
	private int numberOfWeights(int[] nodesPerLayer) {
		int connections = 0;

		for (int i = 0; i < nodesPerLayer.length - 1; i++) {
			connections += nodesPerLayer[i] * nodesPerLayer[i + 1];
		}
		return connections; // Does not include the thresholds!
	}

	/**
	 * Internal method to perform the complexity analysis. Returns the number of neurons in the neural network.
	 * @param nodesPerLayer
	 * @return
	 */
	private int numberOfNeurons(int[] nodesPerLayer) {
		int thresholds = 0;

		for (int i = 1; i < nodesPerLayer.length; i++) {
			thresholds += nodesPerLayer[i];
		}
		return thresholds;
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
