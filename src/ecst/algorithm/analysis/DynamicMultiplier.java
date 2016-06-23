package ecst.algorithm.analysis;

/**
 * This class represents a multiplier after the training phase.
 * 
 * @author Matthias Ring
 * 
 */
public class DynamicMultiplier {

	private String name;
	private Integer factor;

		/**
	 * Constructor.
	 * 
	 * @param name
	 * @param factor
	 */
	public DynamicMultiplier(String name, Integer factor) {
		this.name = name;
		this.factor = factor;
	}

	/**
	 * Returns a string representation of this object.
	 */
	public String toString() {
		return name + ": " + factor;
	}

	/**
	 * Returns the multiplication factor.
	 * @return
	 */
	public Integer getFactor() {
		return factor;
	}

	/**
	 * Sets the multiplication factor.
	 * @param factor
	 */
	public void setFactor(Integer factor) {
		this.factor = factor;
	}

	/**
	 * Returns the name of this multiplicator.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	}
