package ecst.algorithm.classification;

import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import weka.classifiers.Classifier;
import weka.classifiers.trees.RandomForest;

public class RandomForestAdapter extends ClassificationAlgorithm {

	private Parameter seed;
	private Parameter maxDepth;
	private Parameter iterations;
	private Parameter randomAttributes;

	// WOULD REQUIRE WEKA 3.8
	// private Parameter bagSize;
	// private Parameter calculateOutOfBagError;
	// private Parameter storeOutOfBagPredictions;
	// private Parameter outputOutOfBagStatistics;
	// private Parameter printClassifiers;
	// private Parameter attributeImportance;
	// private Parameter slots;
	// private Parameter instancesPerLeaf;
	// private Parameter minimumVariance;
	// private Parameter foldsForBackfitting;
	// private Parameter unclassifiedInstances;
	// private Parameter breakTieRandom;
	// private Parameter debug;
	// private Parameter checkCapabilities;
	// private Parameter decimalPlaces;
	// private Parameter batchSize;

	@Override
	protected void initClassifierParameters() {
		iterations = new Parameter(100, "Number of iterations", Parameter.TYPE.INTEGER, "-I", "classifier.numTrees");
		randomAttributes = new Parameter(0, "Number of attributes to investigate randomly", Parameter.TYPE.INTEGER,
				"-K");
		seed = new Parameter(1, "Seed for random number generator", Parameter.TYPE.INTEGER, "-S");
		maxDepth = new Parameter(0, "Maximum depth of the tree", Parameter.TYPE.INTEGER, "-depth", "classifier.maxDepth");

		// WOULD REQUIRE WEKA 3.8
		// bagSize = new Parameter(100, "Bag size in percent", Parameter.TYPE.INTEGER,
		// "-P");
		// calculateOutOfBagError = new Parameter(false, "Calculate out-of-bag error",
		// Parameter.TYPE.BOOLEAN, "-O");
		// storeOutOfBagPredictions = new Parameter(false, "Store out-of-bag
		// predictions", Parameter.TYPE.BOOLEAN,
		// "-store-out-of-bag-predictions");
		// outputOutOfBagStatistics = new Parameter(false, "Output out-of-bag complexity
		// statistics",
		// Parameter.TYPE.BOOLEAN, "-output-out-of-bag-complexity-statistics");
		// printClassifiers = new Parameter(false, "Print individual classifiers",
		// Parameter.TYPE.BOOLEAN, "-print");
		// attributeImportance = new Parameter(false, "Compute and print attribute
		// importance", Parameter.TYPE.BOOLEAN,
		// "-attribute-importance");
		// slots = new Parameter(1, "Number of execution slots", Parameter.TYPE.INTEGER,
		// "-num-slots");
		// instancesPerLeaf = new Parameter(1, "Minimum number of instances per leaf",
		// Parameter.TYPE.INTEGER, "-M");
		// minimumVariance = new Parameter(1.0e-3, "Minimum variance for split",
		// Parameter.TYPE.DOUBLE, "-V");
		// foldsForBackfitting = new Parameter(0, "Number of folds for backfitting",
		// Parameter.TYPE.INTEGER, "-N");
		// unclassifiedInstances = new Parameter(false, "Allow unclassified instances",
		// Parameter.TYPE.BOOLEAN, "-U");
		// breakTieRandom = new Parameter(false, "Break ties randomly",
		// Parameter.TYPE.BOOLEAN, "-B");
		// debug = new Parameter(false, "Debug mode", Parameter.TYPE.BOOLEAN,
		// "-output-debug-info");
		// checkCapabilities = new Parameter(false, "Do not check capabilities",
		// Parameter.TYPE.BOOLEAN,
		// "-do-not-check-capabilities");
		// decimalPlaces = new Parameter(2, "Decimal places for the output of numbers",
		// Parameter.TYPE.INTEGER,
		// "-num-decimal-places");
		// batchSize = new Parameter(100.0, "Batch size", Parameter.TYPE.DOUBLE,
		// "-batch-size");
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { iterations, randomAttributes, seed, maxDepth };

		// WOULD REQUIRE WEKA 3.8
		// return new Parameter[] { bagSize, calculateOutOfBagError,
		// storeOutOfBagPredictions, outputOutOfBagStatistics,
		// printClassifiers, attributeImportance, iterations, slots, randomAttributes,
		// instancesPerLeaf,
		// minimumVariance, seed, maxDepth, foldsForBackfitting, unclassifiedInstances,
		// breakTieRandom, debug,
		// checkCapabilities, decimalPlaces, batchSize };
	}

	@Override
	public Class<? extends Object> getImplementingClass() {
		return RandomForest.class;
	}

	@Override
	protected Parameter[] getGridSearchParameters() {
		return new Parameter[] { iterations, maxDepth };
	}

	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
	}

	@Override
	protected void addDependencies() {
	}

	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}

	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
