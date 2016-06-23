package ecst.algorithm.featureselection;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.BestFirst;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the best first search implementation in WEKA.
 * 
 * @author Matthias Ring
 *
 */
public class BestFirstAdapter extends WrapperFeatureSelection {
	
	private Parameter startSet;
	private Parameter lookupCacheSize;
	private Parameter searchDirection;
	private Parameter numberOfNonImprovingNodes;

	/**
	 * Initializes the algorithm parameters.
	 */
	protected void initSearchMethodParameters() {
		searchDirection = ParameterUtilities.createSelectedParameter("Search direction", new SelectedParameterItem("Forward", "-D 1"),
				new SelectedParameterItem("Backward", "-D 0"), new SelectedParameterItem("Bidirectional", "-D 2"));
		startSet = new Parameter("", "Start set", Parameter.TYPE.STRING, "-P");
		numberOfNonImprovingNodes = new Parameter(5, "Number of non-improving nodes before terminating", Parameter.TYPE.INTEGER, "-N");
		lookupCacheSize = new Parameter(1, "Size of lookup cache for evaluated subsets", Parameter.TYPE.INTEGER, "-S");
	}

	/**
	 * Returns the algorithm parameters.
	 */
	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { searchDirection, startSet, numberOfNonImprovingNodes, lookupCacheSize };
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return BestFirst.class;
	}

	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return null;
	}
}
