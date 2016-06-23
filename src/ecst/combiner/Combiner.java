package ecst.combiner;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.featureextraction.FeatureExtractor;

/**
 * This class combines the possible classification systems and trains the
 * classification system.
 * 
 * @author Matthias Ring
 * 
 */
public class Combiner {

	private volatile boolean cancel;
	private CombinerInputModel inputModel;
	private List<ProgressListener> listeners;
	private List<CombinerOutputModel> outputModelList;

	/**
	 * Constructor.
	 * 
	 * @param model
	 */
	public Combiner(CombinerInputModel model) {
		this.cancel = false;
		this.inputModel = model;
		this.listeners = new LinkedList<ProgressListener>();
		outputModelList = new LinkedList<CombinerOutputModel>();
	}

	/**
	 * The user has pressed the cancel button.
	 */
	public void pleaseStop() {
		cancel = true;
	}

	/**
	 * Returns the trained classification systems.
	 * 
	 * @return
	 */
	public List<CombinerOutputModel> getOutputModelList() {
		return outputModelList;
	}

	/**
	 * Adds a progress listener to this class.
	 * 
	 * @param listener
	 */
	public synchronized void addProgressListener(ProgressListener listener) {
		listeners.add(listener);
	}

	/**
	 * Removes a progress listener from this class.
	 * 
	 * @param listener
	 */
	public synchronized void removeProgressListener(ProgressListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Informs the progress listeners that progress has been made.
	 * 
	 * @param counter
	 * @param numberOfCombinatinos
	 * @param description
	 */
	private void fireProgress(int counter, int numberOfCombinatinos, String description) {
		ProgressEvent event = new ProgressEvent(this, (int) ((double) counter / (double) numberOfCombinatinos * 100.0), description);

		for (ProgressListener listener : listeners) {
			listener.progressMade(event);
		}
	}

	/**
	 * Highlights the currently executed algorithm.
	 * 
	 * @param highlight
	 * @param content
	 * @return
	 */
	private String buildPipelineStepString(boolean highlight, String content) {
		if (highlight) {
			return /* "<i>" + */content /* + "</i>" */;
		} else {
			return content;
		}
	}

	/**
	 * Builds the description string for this classification system.
	 * 
	 * @param step
	 * @param preprocessing
	 * @param featureSelection
	 * @param classification
	 * @param evaluation
	 * @return
	 */
	private String buildStatusString(int step, PreprocessingAlgorithm preprocessing, FeatureSelectionAlgorithm featureSelection,
			ClassificationAlgorithm classification, EvaluationAlgorithm evaluation) {
		String description = null;

		description = "<html>";
		if (preprocessing != null) {
			description += buildPipelineStepString(step == 0, preprocessing.getDefinition().getName()) + " &rarr; ";
		}
		if (featureSelection != null) {
			description += buildPipelineStepString(step == 1, featureSelection.getDefinition().getName()) + " &rarr; ";
		}
		description += buildPipelineStepString(step == 2, classification.getDefinition().getName()) + " &rarr; ";
		description += buildPipelineStepString(step == 3, evaluation.getDefinition().getName());
		description += "</html>";

		return description.trim();
	}

	/**
	 * Combines all possible classification system and trains them.
	 * 
	 * @throws Exception
	 */
	public void process() throws Exception {
		int taskCounter;
		int numberOfTasks;
		boolean expection;
		long timeStart;
		long timeEnd;
		ArffLoader loader = null;
		PipelineData data = null;
		Instances instances = null;
		Classifier classifier = null;
		Evaluation evaluationResult = null;

		cancel = false;
		expection = false;
		taskCounter = 0;
		numberOfTasks = inputModel.getPreprocessingAlgorithms().size() * inputModel.getFeatureSelectionAlgorithms().size()
				* inputModel.getClassificationAlgorithms().size() * inputModel.getEvaluationAlgorithms().size() * 5;

		if (inputModel.isExtractFeatures()) {
			numberOfTasks++;
			fireProgress(++taskCounter, numberOfTasks, "Extracting features");
			instances = FeatureExtractor.extract(inputModel.getFeatureExtractionModel(), inputModel.getFeatureExtractionAlgorithms());
		} else {
			loader = new ArffLoader();
			loader.setFile(new File(inputModel.getInputFile()));
			instances = loader.getDataSet();
			instances.setClassIndex(instances.numAttributes() - 1);
		}

		for (PreprocessingAlgorithm preprocessing : inputModel.getPreprocessingAlgorithms()) {
			for (FeatureSelectionAlgorithm featureSelection : inputModel.getFeatureSelectionAlgorithms()) {
				for (ClassificationAlgorithm classification : inputModel.getClassificationAlgorithms()) {
					for (EvaluationAlgorithm evaluation : inputModel.getEvaluationAlgorithms()) {
						try {
							timeStart = System.currentTimeMillis();

							classification.readEditorSettings();
							evaluation.readEditorSettings();

							data = new PipelineData(new Instances(instances));
							fireProgress(++taskCounter, numberOfTasks, buildStatusString(0, preprocessing, featureSelection, classification, evaluation));

							if (preprocessing == null) {
								data.setPreprocessedInstances(data.getInstances());
							} else {
								preprocessing.readEditorSettings();
								data.setPreprocessedInstances(preprocessing.preprocess(data));
							}
							fireProgress(++taskCounter, numberOfTasks, buildStatusString(1, preprocessing, featureSelection, classification, evaluation));
							if (cancel) {
								fireProgress(-1, 100, "canceling");
								return;
							}

							if (featureSelection == null) {
								data.setFeatureSelectedInstances(data.getPreprocessedInstances());
							} else {
								featureSelection.readEditorSettings();
								data.setFeatureSelectedInstances(featureSelection.selectFeatures(data, classification.createClassifier()));
							}
							fireProgress(++taskCounter, numberOfTasks, buildStatusString(2, preprocessing, featureSelection, classification, evaluation));
							if (cancel) {
								fireProgress(-1, 100, "canceling");
								return;
							}
							
							//if (1 + 1 == 2) return;

							classifier = classification.classify(data);
							fireProgress(++taskCounter, numberOfTasks, buildStatusString(3, preprocessing, featureSelection, classification, evaluation));
							if (cancel) {
								fireProgress(-1, 100, "canceling");
								return;
							}

							evaluationResult = evaluation.evaluate(data, classifier, featureSelection);
							fireProgress(++taskCounter, numberOfTasks, buildStatusString(4, preprocessing, featureSelection, classification, evaluation));

							timeEnd = System.currentTimeMillis();

							outputModelList.add(new CombinerOutputModel(inputModel.getInputFile(), data, evaluationResult, preprocessing, featureSelection,
									classification, evaluation, inputModel.getFeatureExtractionAlgorithms(), inputModel.getFeatureExtractionModel(), timeEnd
											- timeStart));

							if (cancel) {
								fireProgress(-1, 100, "canceling");
								return;
							}
						} catch (Exception e) {
							e.printStackTrace();
							expection = true;
						}
					}
				}
			}
		}
		if (expection) {
			fireProgress(-1, 100, "canceling");
			throw new Exception("An exception occurred! See console output for details.");
		}
	}

}
