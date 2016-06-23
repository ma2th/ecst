package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.ExhaustiveSearch;
import ecst.algorithm.parameter.Parameter;

/**
 * This class is an adapter for the exhaustive search implementation in WEKA.
 * 
 * @author Matthias Ring
 *
 */
public class ExhaustiveSearchAdapter extends WrapperFeatureSelection {
	
	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initSearchMethodParameters() {
	}

	/**
	 * Returns the algorithm parameters.
	 */
	@Override
	protected Parameter[] getSearchMethodParameters() {
		return null;
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return ExhaustiveSearch.class;
	}
	
	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return null;
	}

}
