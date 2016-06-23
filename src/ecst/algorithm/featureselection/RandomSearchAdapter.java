package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.RandomSearch;
import ecst.algorithm.parameter.Parameter;

/**
 * This class is an adapter for the random search implementation in WEKA.
 * 
 * @author Matthias Ring
 *
 */
public class RandomSearchAdapter extends WrapperFeatureSelection {
	
	private Parameter percent;
	private Parameter startSet;
	
	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	protected void initSearchMethodParameters() {
		startSet = new Parameter("", "Start set", Parameter.TYPE.STRING, "-P");
		percent = new Parameter(25.0, "Percent of search space to consider", Parameter.TYPE.DOUBLE, "-F");
	}
	
	/**
	 * Returns the algorithm parameters.
	 */
	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { percent, startSet };
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return RandomSearch.class;
	}
	
	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return null;
	}
	
}
