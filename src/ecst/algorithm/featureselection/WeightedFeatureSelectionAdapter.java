package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import ecst.algorithm.featureselection.search.WeightedFeatureSelection;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the weighted feature selection implementation.
 * 
 * @author Matthias Ring
 *
 */
public class WeightedFeatureSelectionAdapter extends WrapperFeatureSelection {
	
	private Parameter minWeight;
	private Parameter maxWeight;
	private Parameter weights;
	private Parameter searchMethod;

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initSearchMethodParameters() {
		minWeight = new Parameter("", "Minimum weight (empty for no minimum)", Parameter.TYPE.STRING, "-L");
		maxWeight = new Parameter("", "Maximum weight (empty for no maximum)", Parameter.TYPE.STRING, "-U");
		weights = new Parameter("", "Comma-separated weights for each feature (empty string for equal weights)", Parameter.TYPE.STRING,
				"-W");
		searchMethod = ParameterUtilities.createSelectedParameter("Search method", new SelectedParameterItem("Exhaustive", "-E"),
				new SelectedParameterItem("Sequential forward", "-F"), new SelectedParameterItem("Sequential backward", "-B"));
	}

	/**
	 * Returns the algorithm parameters.
	 */
	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { minWeight, maxWeight, weights, searchMethod };
	}
	
	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return WeightedFeatureSelection.class;
	}
	
	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return null;
	}

}
