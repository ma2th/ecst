package ecst.algorithm;

import weka.classifiers.Classifier;
import weka.classifiers.meta.GridSearch;
import ecst.algorithm.classification.GridSearchManager;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * Abstract basis class that all classification algorithms have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class ClassificationAlgorithm extends WorkPhaseAlgorithm {

	private String modelString;
	private GridSearchManager gridSearchManager;

	/**
	 * This method has to initialize the parameters of the algorithm.
	 */
	protected abstract void initClassifierParameters();

	/**
	 * This method has to return the parameters that belong to the grid search
	 * settings.
	 */
	protected abstract Parameter[] getGridSearchParameters();

	/**
	 * This method is called after the classification process, but before the
	 * complexity analysis is done. Additional tasks after the training can be
	 * performed here.
	 * 
	 * @param classifier
	 *            the trained classifer
	 * @param data
	 *            the data for the pattern recognition pipeline
	 */
	protected abstract void postprocess(Classifier classifier, PipelineData data) throws Exception;

	/**
	 * This method has to prepare the complexity analysis. Parameters from the
	 * training process that are necessary for the analysis can be extracted
	 * here.
	 * 
	 * @param classifier
	 *            the trained classifer
	 * @param data
	 *            the data for the pattern recognition pipeline
	 */
	protected abstract void analyzeSystem(PipelineData data, Classifier classifier) throws Exception;

	/**
	 * Initializes the parameters of the classifier.
	 */
	@Override
	public void initParameters() {
		initClassifierParameters();

		if (getGridSearchParameters() != null) {
			gridSearchManager = new GridSearchManager(getGridSearchParameters());
			getEditor().setEnableGridSearch(gridSearchManager.getEnableGridSearch());
			getEditor().addGridSearchParameters(gridSearchManager.getParameters(), gridSearchManager.getImplementingClass());
		}
	}

	/**
	 * Creates a new instance of this classifier.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Classifier createClassifier() throws Exception {
		Classifier classifier = null;
		
		classifier = (Classifier) getImplementingClass().newInstance();
		classifier.setOptions(ParameterUtilities.buildOptionsString(this));
		
		return classifier;
	}

	/**
	 * Trains this classifier and afterwards performs the complexity analysis.
	 * 
	 * Attention: this method must be called with the complete data set, i.e.
	 * not during n-fold cross-validation!!!
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Classifier classify(PipelineData data) throws Exception {
		Classifier classifier = null;
		
		if (gridSearchManager != null && (Boolean) gridSearchManager.getEnableGridSearch().getValue()) {
			classifier = gridSearchManager.createClassifier(getImplementingClass().getCanonicalName(), getParameters());
			classifier.buildClassifier(data.getFeatureSelectedInstances());
			classifier = ((GridSearch) classifier).getBestClassifier();
			classifier.buildClassifier(data.getFeatureSelectedInstances());
			gridSearchManager.saveResult(classifier, getImplementingClass());
		} else {
			classifier = createClassifier();
			classifier.buildClassifier(data.getFeatureSelectedInstances());
		}
		modelString = classifier.toString();
		postprocess(classifier, data);
		analyzeSystem(data, classifier);
		saveAnalysis();

		return classifier;
	}

	/**
	 * Return the parameters for grid search if enabled for this classifier.
	 * 
	 * @return
	 */
	public Parameter[] getGridSearchManagerParameter() {
		if (gridSearchManager == null) {
			return new Parameter[] {};
		}
		return gridSearchManager.getParameters();
	}

	/**
	 * Returns if grid search is enabled for this classifier.
	 * 
	 * @return
	 */
	public boolean isGridSearch() {
		return gridSearchManager != null && (Boolean) gridSearchManager.getEnableGridSearch().getValue();
	}

	/**
	 * Returns the parameter that belongs to the JCheckBox which the user can
	 * select to enable grid search.
	 * 
	 * @return
	 */
	public Parameter getEnableGridSearchParameter() {
		if (gridSearchManager == null) {
			return null;
		} else {
			return gridSearchManager.getEnableGridSearch();
		}
	}

	/**
	 * Returns the name of the first parameter for the grid search.
	 * 
	 * @return
	 */
	public String getGridSearchXName() {
		return ((SelectedParameter) gridSearchManager.getGridX().getValue()).getItems()
				.get(((SelectedParameter) gridSearchManager.getGridX().getValue()).getSelectedIndex()).getDisplayName();
	}

	/**
	 * Returns the name of the second parameter for the grid search.
	 * 
	 * @return
	 */
	public String getGridSearchYName() {
		return ((SelectedParameter) gridSearchManager.getGridY().getValue()).getItems()
				.get(((SelectedParameter) gridSearchManager.getGridY().getValue()).getSelectedIndex()).getDisplayName();
	}

	/**
	 * Returns the result of the grid search for the first parameter.
	 * 
	 * @return
	 */
	public Object getGridSearchXValue() {
		return gridSearchManager.getGridSearchXValue();
	}

	/**
	 * Returns the result of the grid search for the second parameter.
	 * 
	 * @return
	 */
	public Object getGridSearchYValue() {
		return gridSearchManager.getGridSearchYValue();
	}

	/**
	 * Returns a string that describes the trained classifier.
	 * 
	 * @return
	 */
	public String getModelString() {
		return modelString;
	}

}
