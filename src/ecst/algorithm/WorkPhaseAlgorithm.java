package ecst.algorithm;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.analysis.DynamicOperations;
import ecst.algorithm.analysis.DynamicSpace;
import ecst.algorithm.definition.StaticOperations;
import ecst.algorithm.definition.StaticSpace;
import ecst.combiner.PipelineData;

/**
 * Basis class that all working phase algorithms have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class WorkPhaseAlgorithm extends Algorithm {

	private List<DynamicSpace> dynamicSpaceList;
	private List<DynamicOperations> dynamicOperationsList;

	/**
	 * This method has to add the complexity measures that are depended on the
	 * data or the user settings.
	 */
	protected abstract void addDependencies();

	/**
	 * This method has to set the value of the given multiplier.
	 * 
	 * @param multiplier
	 */
	protected abstract void setMultiplier(DynamicMultiplier multiplier);

	/**
	 * This method has to create a XML string that describes this algorithm
	 * after the training phase is complete.
	 * 
	 * @param data
	 * @return
	 */
	public abstract String modelToXML(PipelineData data);

	/***
	 * This method saves the complexity analysis.
	 */
	protected void saveAnalysis() {
		resetDynamicAnalysis();
		addDependencies();
		// use getter here (getDynamicOperationsList()) to enable combined algorithms like OutlierDetection and Normalization!!!
		for (DynamicOperations operations : getDynamicOperationsList()) { 
			setMultipliers(operations.getMultiplierList());
		}
		// use getter here (getDynamicSpaceList()) to enable combined algorithms like OutlierDetection and Normalization!!!
		for (DynamicSpace space : getDynamicSpaceList()) {
			setMultipliers(space.getMultiplierList());
		}
	}

	/**
	 * Internal method to evaluate all multipliers.
	 * 
	 * @param list
	 */
	private void setMultipliers(List<DynamicMultiplier> list) {
		for (DynamicMultiplier multiplier : list) {
			setMultiplier(multiplier);
		}
	}

	/**
	 * Returns the list of space requirements.
	 * 
	 * @return
	 */
	public List<DynamicSpace> getDynamicSpaceList() {
		return dynamicSpaceList;
	}

	/**
	 * Returns the list of necessary operations for one classification decision.
	 * 
	 * @return
	 */
	public List<DynamicOperations> getDynamicOperationsList() {
		return dynamicOperationsList;
	}

	/**
	 * Adds complexity measures that are dependent on the data or the user
	 * settings and were not added by default.
	 * 
	 * @param dependency
	 */
	public void addDependency(String dependency) {
		for (StaticOperations operations : getDefinition().getStaticOperationsListByDependency(dependency)) {
			dynamicOperationsList.add(new DynamicOperations(operations));
		}
		for (StaticSpace space : getDefinition().getStaticSpaceListByDependency(dependency)) {
			dynamicSpaceList.add(new DynamicSpace(space));
		}
	}
	
	/**
	 * Adds complexity measures that are dependent on the data or the user
	 * settings and were not added by default.
	 * 
	 * @param dependency
	 */
	public void addDependency(String dependency, String[] dependsOnFeature) {
		for (StaticOperations operations : getDefinition().getStaticOperationsListByDependency(dependency)) {
			dynamicOperationsList.add(new DynamicOperations(operations, dependsOnFeature));
		}
		for (StaticSpace space : getDefinition().getStaticSpaceListByDependency(dependency)) {
			dynamicSpaceList.add(new DynamicSpace(space, dependsOnFeature));
		}
	}

	/**
	 * Reset the complexity analysis.
	 */
	public void resetDynamicAnalysis() {
		dynamicOperationsList = new LinkedList<DynamicOperations>();
		for (StaticOperations operations : getDefinition().getStaticOperationsList()) {
			if (!operations.isDependent()) {
				dynamicOperationsList.add(new DynamicOperations(operations));
			}
		}

		dynamicSpaceList = new LinkedList<DynamicSpace>();
		for (StaticSpace space : getDefinition().getStaticSpaceList()) {
			if (!space.isDependent()) {
				dynamicSpaceList.add(new DynamicSpace(space));
			}
		}
	}

}
