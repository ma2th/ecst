package ecst.algorithm.analysis;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.definition.StaticOperations;

/**
 * This class represents the the StaticOperations object after the training
 * phase, i.e. including the multipliers.
 * 
 * @author Matthias Ring
 * 
 */
public class DynamicOperations {

	private List<DynamicMultiplier> multiplierList;
	private StaticOperations staticOperations;
	private String[] dependsOnFeatures;

	/**
	 * Forward constructor.
	 * 
	 * @param operations
	 */
	public DynamicOperations(StaticOperations operations) {
		this(operations, null);
	}
	
	/**
	 * Constructor.
	 */
	public DynamicOperations(StaticOperations operations, String[] dependsOnFeatures) {
		this.staticOperations = operations;
		this.multiplierList = new LinkedList<DynamicMultiplier>();
		for (String multiplierName : operations.getMultiplierNamesList()) {
			multiplierList.add(new DynamicMultiplier(multiplierName, null));
		}
		setDependsOnFeatures(dependsOnFeatures);
	}	
	

	/**
	 * Returns the definition from the XML configuration document.
	 * 
	 * @return
	 */
	public StaticOperations getStaticOperations() {
		return staticOperations;
	}

	/**
	 * Returns the list of multipliers for this operations object.
	 * 
	 * @return
	 */
	public List<DynamicMultiplier> getMultiplierList() {
		return multiplierList;
	}

	/**
	 * Returns the feature that this operations depends on. If the feature is
	 * not selected in feature selection, this operations will not be considered
	 * in the analysis.
	 * 
	 * @return
	 */
	public String[] getDependsOnFeatures() {
		return dependsOnFeatures;
	}

	/**
	 * Sets the feature that this operations depends on.If the feature is not
	 * selected in feature selection, this operations will not be considered in
	 * the analysis.
	 * 
	 * @param dependsOnFeature
	 */
	public void setDependsOnFeatures(String[] dependsOnFeatures) {
		this.dependsOnFeatures = dependsOnFeatures;
	}
}
