package ecst.algorithm.definition;

import java.util.List;

/**
 * This class represents one space tag in XML configuration document.
 * 
 * @author Matthias Ring
 * 
 */
public class StaticSpace {

	public static final String INTEGER_TYPE = "integer";
	public static final String DOUBLE_TYPE = "double";

	private String type;
	private String dependency;
	private String description;
	private List<String> multiplier;

	/**
	 * Constructor.
	 * 
	 * @param type
	 * @param description
	 * @param multiplier
	 * @param dependency
	 */
	public StaticSpace(String type, String description, List<String> multiplier, String dependency) {
		if (!(INTEGER_TYPE.equals(type) || DOUBLE_TYPE.equals(type))) {
			throw new IllegalArgumentException("Unknown space type: " + type);
		}
		this.type = type;
		this.description = description;
		this.multiplier = multiplier;
		this.dependency = dependency;
	}

	/**
	 * Returns true if a dependency is set.
	 * 
	 * @return
	 */
	public boolean isDependent() {
		return dependency != null;
	}

	/**
	 * Returns the name of the dependency.
	 * 
	 * @return
	 */
	public String getDependency() {
		return dependency;
	}

	/**
	 * Returns the description for this space requirement.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the list of defined multipliers.
	 * 
	 * @return
	 */
	public List<String> getMultiplierList() {
		return multiplier;
	}

	/**
	 * Returns the type of this space requirement.
	 * 
	 * @return
	 */
	public String getType() {
		return type;
	}
}
