package ecst.algorithm.classification;

import java.util.Map;

import weka.classifiers.Classifier;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;

/**
 * This class is a adapter to the ExternalClassifier algorithm to include
 * external programs as classification algorithms.
 * 
 * @author Matthias Ring
 * 
 */
public class ExternalClassifierAdapter extends ClassificationAlgorithm {

	private Parameter command;
	private Parameter trainScript;
	private Parameter classifyScript;
	private Map<String, Integer> externalMultiplier;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initClassifierParameters() {
		command = new Parameter("matlab -nodesktop -nosplash -r", "Command to execute the train and classify script",
				Parameter.TYPE.STRING, "-E");
		trainScript = new Parameter("classifyTraining", "Script to train the classifier with the instances in 'instances.csv'",
				Parameter.TYPE.STRING, "-T");
		classifyScript = new Parameter("classifyClassification", "Script to classify the instance in 'instance.csv'",
				Parameter.TYPE.STRING, "-C");
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { command, classifyScript, trainScript };
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
		return ExternalClassifier.class;
	}

	/**
	 * Analyzes the trained classifier to perform the complexity analysis later.
	 */
	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
		externalMultiplier = ((ExternalClassifier) classifier).getExternalMultiplier();
	}

	/**
	 * Sets the factors of the algorithm-specific multipliers.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
		if (externalMultiplier.containsKey(multiplier.getName())) {
			multiplier.setFactor(externalMultiplier.get(multiplier.getName()));
		}
	}

	/**
	 * No dependencies for this algoritm.
	 */
	@Override
	protected void addDependencies() {
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
