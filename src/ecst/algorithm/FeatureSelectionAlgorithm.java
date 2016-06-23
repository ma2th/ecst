package ecst.algorithm;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.classifiers.Classifier;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * Basis class that all feature selection algorithms have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class FeatureSelectionAlgorithm extends Algorithm {

	private String additionalInfo;

	/**
	 * This method has to initialize the parameters that belong to the search
	 * method.
	 * 
	 */
	protected abstract void initSearchMethodParameters();

	/**
	 * This method has to return all parameters that belong to the search
	 * method.
	 * 
	 * @return
	 */
	protected abstract Parameter[] getSearchMethodParameters();

	/**
	 * This method has to initialize the parameters that belong to the feature
	 * evaluation algorithm.
	 */
	protected abstract void initEvaluatorParameters();

	/**
	 * This method has to return all parameters that belong to the feature
	 * evaluation algorithm.
	 * 
	 * @return
	 */
	protected abstract Parameter[] getEvaluatorParameters();

	/**
	 * This method has to create a new instance of the feature evaluation
	 * algorithm.
	 * 
	 * @param classifier
	 *            the used classifier
	 * @param data
	 *            the data of the pattern recognition pipeline
	 * @return
	 */
	public abstract ASEvaluation createEvaluator(Classifier classifier, PipelineData data);

	/**
	 * This method provides the algorithm the opportunity to return a string
	 * with detailed information about the search process.
	 * 
	 * @return
	 */
	protected abstract String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator);

	public String getAdditionalInformation() {
		return additionalInfo;
	}

	/**
	 * Initializes the parameters of this algorithm.
	 */
	@Override
	public void initParameters() {
		initEvaluatorParameters();
		initSearchMethodParameters();
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return ParameterUtilities.mergeParameters(getEvaluatorParameters(), getSearchMethodParameters());
	}

	/**
	 * Creates a new instance of the search algorithm.
	 * 
	 * @return
	 * @throws Exception
	 */
	public ASSearch createSearchMethod() throws Exception {
		ASSearch searchMethod = (ASSearch) getImplementingClass().newInstance();
		if (searchMethod instanceof OptionHandler && getSearchMethodParameters() != null) {
			((OptionHandler) searchMethod).setOptions(Utils.splitOptions(ParameterUtilities.buildOptionsString(getSearchMethodParameters())));
		}
		return searchMethod;
	}

	/**
	 * Performs the feature selection and returns a copy of the data with only
	 * the selected features.
	 * 
	 * Attention: this method must be called with the complete data set, i.e.
	 * not during n-fold cross-validation!!!
	 * 
	 * @param data
	 * @param classifier
	 * @return
	 * @throws Exception
	 */
	public Instances selectFeatures(PipelineData data, Classifier classifier) throws Exception {
		ASEvaluation evaluator = null;
		Remove removeFilter = new Remove();
		ASSearch search = createSearchMethod();
		AttributeSelection attributeSelection = new AttributeSelection();

		attributeSelection.setSearch(search);
		evaluator = createEvaluator(classifier, data);
		attributeSelection.setEvaluator(evaluator);
		attributeSelection.SelectAttributes(data.getPreprocessedInstances());
		additionalInfo = getAdditionalInformationAboutSearchProcess(search, evaluator);

		removeFilter.setAttributeIndicesArray(attributeSelection.selectedAttributes());
		removeFilter.setInvertSelection(true);
		removeFilter.setInputFormat(data.getPreprocessedInstances());

		return Filter.useFilter(data.getPreprocessedInstances(), removeFilter);
	}
}
