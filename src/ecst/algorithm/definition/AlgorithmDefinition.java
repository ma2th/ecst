package ecst.algorithm.definition;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.Algorithm;

/**
 * This class represents the algorithm definition the XML document.
 * 
 * @author Matthias Ring
 * 
 */
public class AlgorithmDefinition implements Comparable<AlgorithmDefinition> {

	private String name;
	private String className;
	private List<StaticSpace> staticSpaceList;
	private List<StaticOperations> staticOperationsList;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param className
	 */
	public AlgorithmDefinition(String name, String className) {
		this.name = name;
		this.className = className;
		this.staticSpaceList = new LinkedList<StaticSpace>();
		this.staticOperationsList = new LinkedList<StaticOperations>();
	}

	/**
	 * Creates a new instance of this algorithm definition.
	 * 
	 * @return
	 * @throws Exception
	 */
	public Algorithm createInstance() throws Exception {
		Algorithm algorithm = null;

		algorithm = (Algorithm) Class.forName(getClassName()).newInstance();
		algorithm.setDefinition(this);
		algorithm.init();

		return algorithm;
	}

	/**
	 * Compares this algorithm to another by the algorithm name.
	 */
	@Override
	public int compareTo(AlgorithmDefinition o) {
		return getName().compareTo(o.getName());
	}

	/**
	 * Returns a string representation of this algorithm definition.
	 */
	public String toString() {
		return "name: " + getName() + ", class name: " + getClassName();
	}

	/**
	 * Returns the algorithm name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the name of the implementing class.
	 * 
	 * @return
	 */
	public String getClassName() {
		return className;
	}

	/**
	 * Returns the list of space definitions.
	 * 
	 * @return
	 */
	public List<StaticSpace> getStaticSpaceList() {
		return staticSpaceList;
	}

	/**
	 * Adds a new space definition from the XML document.
	 * 
	 * @param staticSpace
	 */
	public void addStaticSpace(StaticSpace staticSpace) {
		staticSpaceList.add(staticSpace);
	}

	/**
	 * Returns the space definition that depend on the given argument.
	 * 
	 * @param dependency
	 * @return
	 */
	public List<StaticSpace> getStaticSpaceListByDependency(String dependency) {
		List<StaticSpace> list = new LinkedList<StaticSpace>();

		for (StaticSpace space : staticSpaceList) {
			if (dependency.equals(space.getDependency())) {
				list.add(space);
			}
		}

		return list;
	}

	/**
	 * Returns the operations defined in the XML document.
	 * 
	 * @return
	 */
	public List<StaticOperations> getStaticOperationsList() {
		return staticOperationsList;
	}

	/**
	 * Adds a new operations definition from the XML document
	 * 
	 * @param operations
	 */
	public void addStaticOperations(StaticOperations operations) {
		staticOperationsList.add(operations);
	}

	/**
	 * Returns the operations definition that depend on the given argument.
	 * 
	 * @param dependency
	 * @return
	 */
	public List<StaticOperations> getStaticOperationsListByDependency(String dependency) {
		List<StaticOperations> list = new LinkedList<StaticOperations>();

		for (StaticOperations operation : staticOperationsList) {
			if (dependency.equals(operation.getDependency())) {
				list.add(operation);
			}
		}

		return list;
	}

}
