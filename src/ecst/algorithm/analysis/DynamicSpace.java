package ecst.algorithm.analysis;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.definition.StaticSpace;

/**
 * This class represents the the StaticSpace object after the training phase,
 * i.e. including the multipliers.
 * 
 * @author Matthias Ring
 * 
 */
public class DynamicSpace {

	private List<DynamicMultiplier> multiplierList;
	private StaticSpace staticSpace;
	private String[] dependsOnFeatures;

	/**
	 * Forward constructor.
	 * 
	 * @param staticSpace
	 */
	public DynamicSpace(StaticSpace staticSpace) {
		this(staticSpace, null);
	}
	
	/**
	 * Constructor.
	 */
	public DynamicSpace(StaticSpace staticSpace, String[] dependsOnFeatures) {
		this.staticSpace = staticSpace;
		this.multiplierList = new LinkedList<DynamicMultiplier>();
		for (String multiplierName : staticSpace.getMultiplierList()) {
			multiplierList.add(new DynamicMultiplier(multiplierName, null));
		}
		setDependsOnFeatures(dependsOnFeatures);
	}

	/**
	 * Returns the definition from the XML configuration document.
	 * 
	 * @return
	 */
	public StaticSpace getStaticSpace() {
		return staticSpace;
	}

	/**
	 * Returns the list of multipliers for this space object.
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
