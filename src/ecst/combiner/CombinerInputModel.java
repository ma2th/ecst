package ecst.combiner;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureExtractionAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.featureextraction.FeatureExtractionModel;

/**
 * This class represents the settings that the user has made in the GUI. This
 * class is the input for the Combiner class.
 * 
 * @author Matthias Ring
 * 
 */
public class CombinerInputModel {

	private String inputFile;
	private boolean extractFeatures;
	private FeatureExtractionModel featureExtractionModel;
	private List<FeatureExtractionAlgorithm> featureExtractionAlgorithms;
	private List<PreprocessingAlgorithm> preprocessingAlgorithms;
	private List<FeatureSelectionAlgorithm> featureSelectionAlgorithms;
	private List<ClassificationAlgorithm> classificationAlgorithms;
	private List<EvaluationAlgorithm> evaluationAlgorithms;

	/**
	 * Constructor.
	 */
	public CombinerInputModel() {
		featureExtractionAlgorithms = new LinkedList<FeatureExtractionAlgorithm>();
		preprocessingAlgorithms = new LinkedList<PreprocessingAlgorithm>();
		featureSelectionAlgorithms = new LinkedList<FeatureSelectionAlgorithm>();
		classificationAlgorithms = new LinkedList<ClassificationAlgorithm>();
		evaluationAlgorithms = new LinkedList<EvaluationAlgorithm>();
	}

	/**
	 * Returns if feature extraction should be performed.
	 * 
	 * @return
	 */
	public boolean isExtractFeatures() {
		return extractFeatures;
	}

	/**
	 * Sets if feature extraction should be performed.
	 * 
	 * @param extractFeatures
	 */
	public void setExtractFeatures(boolean extractFeatures) {
		this.extractFeatures = extractFeatures;
	}

	/**
	 * Returns the input file.
	 * 
	 * @return
	 */
	public String getInputFile() {
		return inputFile;
	}

	/**
	 * Sets the input file.
	 * 
	 * @param inputFile
	 */
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * Returns the settings for feature extraction.
	 * @return
	 */
	public FeatureExtractionModel getFeatureExtractionModel() {
		return featureExtractionModel;
	}

	/**
	 * Sets the settings for feature extraction.
	 * @param featureExtractionModel
	 */
	public void setFeatureExtractionModel(FeatureExtractionModel featureExtractionModel) {
		this.featureExtractionModel = featureExtractionModel;
	}

	/**
	 * Adds a feature extraction algorithm.
	 * @param featureExtractionAlgorithm
	 */
	public void addFeatureExtractionAlgorithm(FeatureExtractionAlgorithm featureExtractionAlgorithm) {
		featureExtractionAlgorithms.add(featureExtractionAlgorithm);
	}

	/**
	 * Adds a preprocessing algorithm.
	 */
	public void addPreprocessingAlgorithm(PreprocessingAlgorithm preprocessingAlgorithm) {
		preprocessingAlgorithms.add(preprocessingAlgorithm);
	}

	/**
	 * Adds a feature selection algorithm.
	 */
	public void addFeatureSelectionAlgorithm(FeatureSelectionAlgorithm featureSelectionAlgorithm) {
		featureSelectionAlgorithms.add(featureSelectionAlgorithm);
	}

	/**
	 * Adds a classification algorithm.
	 */
	public void addClassificationAlgorithm(ClassificationAlgorithm classificationAlgorithm) {
		classificationAlgorithms.add(classificationAlgorithm);
	}

	/**
	 * Adds a evaluation algorithm.
	 */
	public void addEvaluationAlgorithm(EvaluationAlgorithm evaluationAlgorithm) {
		evaluationAlgorithms.add(evaluationAlgorithm);
	}

	/**
	 * Returns all feature extraction algorithms.
	 */
	public List<FeatureExtractionAlgorithm> getFeatureExtractionAlgorithms() {
		return featureExtractionAlgorithms;
	}

	/**
	 * Returns all preprocessing algorithms.
	 */
	public List<PreprocessingAlgorithm> getPreprocessingAlgorithms() {
		return preprocessingAlgorithms;
	}

	/**
	 * Returns all feature selection algorithms.
	 */
	public List<FeatureSelectionAlgorithm> getFeatureSelectionAlgorithms() {
		return featureSelectionAlgorithms;
	}

	/**
	 * Returns all classification algorithms.
	 */
	public List<ClassificationAlgorithm> getClassificationAlgorithms() {
		return classificationAlgorithms;
	}

	/**
	 * Returns all evaluation algorithms.
	 */
	public List<EvaluationAlgorithm> getEvaluationAlgorithms() {
		return evaluationAlgorithms;
	}

}
