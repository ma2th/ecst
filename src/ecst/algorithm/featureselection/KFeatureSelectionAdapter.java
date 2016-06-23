package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import ecst.algorithm.featureselection.search.KFeatureSelection;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the sequential feature selection implementation.
 * 
 * @author Matthias Ring
 * 
 */
public class KFeatureSelectionAdapter extends WrapperFeatureSelection {

	private Parameter searchMethod;
	private Parameter numberOfFeatures;

	@Override
	protected void initSearchMethodParameters() {
		numberOfFeatures = new Parameter(1, "Number of features to select", Parameter.TYPE.INTEGER, "-N");
		searchMethod = ParameterUtilities.createSelectedParameter("Search method", new SelectedParameterItem("Sequential forward", "-F"),
				new SelectedParameterItem("Sequential backward", "-B"), new SelectedParameterItem("Exhaustive", "-E"));
	}

	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { searchMethod, numberOfFeatures };
	}

	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return "Search time: " + ((KFeatureSelection) search).getExecutionTime() + " ms";
	}

	@Override
	public Class<? extends Object> getImplementingClass() {
		return KFeatureSelection.class;
	}

}
