package ecst.combiner;

import java.util.LinkedList;
import java.util.List;

import weka.classifiers.Evaluation;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureExtractionAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.algorithm.analysis.DynamicOperations;
import ecst.algorithm.analysis.DynamicSpace;
import ecst.featureextraction.FeatureExtractionModel;

/**
 * This class represents the trained pattern recognition pipeline. This class is
 * the output of the Combiner class.
 * 
 * @author Matthias Ring
 * 
 */
public class CombinerOutputModel implements Comparable<CombinerOutputModel> {

	private long time;
	private String filename;
	private PipelineData data;
	private Evaluation evaluationResult;
	private List<DynamicSpace> preprocessingSpace;
	private List<DynamicSpace> classificationSpace;
	private List<DynamicSpace> featureExtractionSpace;
	private List<DynamicOperations> preprocessingOperations;
	private List<DynamicOperations> classificationOperations;
	private List<DynamicOperations> featureExtractionOperations;
	private PreprocessingAlgorithm preprocessingAlgorithm;
	private FeatureSelectionAlgorithm featureSelectionAlgorithm;
	private ClassificationAlgorithm classificationAlgorithm;
	private EvaluationAlgorithm evaluationAlgorithm;
	private List<FeatureExtractionAlgorithm> featureExtractionAlgorithms;
	private FeatureExtractionModel featureExtractionModel;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 * @param data
	 * @param evaluationResult
	 * @param preprocessingAlgorithm
	 * @param featureSelectionAlgorithm
	 * @param classificationAlgorithm
	 * @param evaluationAlgorithm
	 * @param featureExtractionAlgorithms
	 * @param featureExtractionModel
	 */
	public CombinerOutputModel(String filename, PipelineData data, Evaluation evaluationResult, PreprocessingAlgorithm preprocessingAlgorithm,
			FeatureSelectionAlgorithm featureSelectionAlgorithm, ClassificationAlgorithm classificationAlgorithm, EvaluationAlgorithm evaluationAlgorithm,
			List<FeatureExtractionAlgorithm> featureExtractionAlgorithms, FeatureExtractionModel featureExtractionModel, long time) {

		this.filename = filename;
		this.preprocessingAlgorithm = preprocessingAlgorithm;
		this.featureSelectionAlgorithm = featureSelectionAlgorithm;
		this.classificationAlgorithm = classificationAlgorithm;
		this.evaluationAlgorithm = evaluationAlgorithm;
		this.featureExtractionAlgorithms = featureExtractionAlgorithms;
		this.data = data;
		this.time = time;
		this.evaluationResult = evaluationResult;
		this.featureExtractionModel = featureExtractionModel;
		this.classificationOperations = classificationAlgorithm.getDynamicOperationsList();
		this.classificationSpace = classificationAlgorithm.getDynamicSpaceList();
		if (preprocessingAlgorithm != null) {
			this.preprocessingSpace = preprocessingAlgorithm.getDynamicSpaceList();
			this.preprocessingOperations = preprocessingAlgorithm.getDynamicOperationsList();
		}
		if (featureExtractionModel != null) {
			this.featureExtractionSpace = new LinkedList<DynamicSpace>();
			for (FeatureExtractionAlgorithm algorithm : featureExtractionAlgorithms) {
				featureExtractionSpace.addAll(algorithm.getDynamicSpaceList());
			}
			this.featureExtractionOperations = new LinkedList<DynamicOperations>();
			for (FeatureExtractionAlgorithm algorithm : featureExtractionAlgorithms) {
				featureExtractionOperations.addAll(algorithm.getDynamicOperationsList());
			}
		}
	}

	/**
	 * Compares this object to another according to the classification accuracy.
	 */
	@Override
	public int compareTo(CombinerOutputModel model) {
		return new Double(model.getEvaluationResult().pctCorrect()).compareTo(getEvaluationResult().pctCorrect());
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	/**
	 * Return the data object.
	 * 
	 * @return
	 */
	public PipelineData getData() {
		return data;
	}

	/**
	 * Returns the name of the input file.
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Returns the evaluation result.
	 * 
	 * @return
	 */
	public Evaluation getEvaluationResult() {
		return evaluationResult;
	}

	/**
	 * Returns the employed preprocessing algorithm.
	 * 
	 * @return
	 */
	public PreprocessingAlgorithm getPreprocessingAlgorithm() {
		return preprocessingAlgorithm;
	}

	/**
	 * Returns the employed feature selection algorithm.
	 * 
	 * @return
	 */
	public FeatureSelectionAlgorithm getFeatureSelectionAlgorithm() {
		return featureSelectionAlgorithm;
	}

	/**
	 * Returns the employed classification algorithm.
	 * 
	 * @return
	 */
	public ClassificationAlgorithm getClassificationAlgorithm() {
		return classificationAlgorithm;
	}

	/**
	 * Returns the employed evaluation algorithm.
	 * 
	 * @return
	 */
	public EvaluationAlgorithm getEvaluationAlgorithm() {
		return evaluationAlgorithm;
	}

	/**
	 * Returns the operations for the preprocessing step.
	 * 
	 * @return
	 */
	public List<DynamicOperations> getPreprocessingOperations() {
		return preprocessingOperations;
	}

	/**
	 * Returns the operations for the classification step.
	 * 
	 * @return
	 */
	public List<DynamicOperations> getClassificationOperations() {
		return classificationOperations;
	}

	/**
	 * Returns the space for the preprocessing step.
	 * 
	 * @return
	 */
	public List<DynamicSpace> getPreprocessingSpace() {
		return preprocessingSpace;
	}

	/**
	 * Returns the space for the classification step.
	 * 
	 * @return
	 */
	public List<DynamicSpace> getClassificationSpace() {
		return classificationSpace;
	}

	/**
	 * Returns the employed feature extraction algorithm.
	 * 
	 * @return
	 */
	public List<FeatureExtractionAlgorithm> getFeatureExtractionAlgorithms() {
		return featureExtractionAlgorithms;
	}

	/**
	 * Returns the definitions for the feature extraction step.
	 * 
	 * @return
	 */
	public FeatureExtractionModel getFeatureExtractionModel() {
		return featureExtractionModel;
	}

	/**
	 * Returns the space for the feature extraction step.
	 * 
	 * @return
	 */
	public List<DynamicSpace> getFeatureExtractionSpace() {
		return featureExtractionSpace;
	}

	/**
	 * Returns the operations for the feature extraction step.
	 * 
	 * @return
	 */
	public List<DynamicOperations> getFeatureExtractionOperations() {
		return featureExtractionOperations;
	}

}
