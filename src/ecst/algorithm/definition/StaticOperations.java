package ecst.algorithm.definition;

import java.util.List;
import java.util.Map;

/**
 * This class represents one operations tag in XML configuration document.
 * 
 * @author Matthias Ring
 * 
 */
public class StaticOperations {

	private String dependency;
	private List<String> multiplierNames;
	private Map<OperationDefinition, Integer> operationDefinitions;

	/**
	 * Constructor.
	 * 
	 * @param operationDefinitions
	 * @param dependency
	 * @param multiplierNames
	 */
	public StaticOperations(Map<OperationDefinition, Integer> operationDefinitions, String dependency, List<String> multiplierNames) {
		this.dependency = dependency;
		this.multiplierNames = multiplierNames;
		this.operationDefinitions = operationDefinitions;
	}

	/**
	 * Returns true if a dependency is set.
	 * @return
	 */
	public boolean isDependent() {
		return dependency != null;
	}

	/**
	 * Returns the name of the dependency.
	 * @return
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Returns the list of defined multipliers.
	 * @return
	 */
	public List<String> getMultiplierNamesList() {
		return multiplierNames;
	}

	/**
	 * Returns the list of defined operations.
	 * @return
	 */
	public Map<OperationDefinition, Integer> getOperationDefinitions() {
		return operationDefinitions;
	}

}
