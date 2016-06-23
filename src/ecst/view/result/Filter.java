package ecst.view.result;

import java.util.Map;

import ecst.algorithm.definition.OperationDefinition;

/**
 * A object to save the filter settings of the user.
 * 
 * @author Matthias Ring
 * 
 */
public class Filter {

	private Integer totalOperations;
	private Integer totalSpace;
	private Integer numberOfDoubles;
	private Integer numberOfIntegers;
	private Map<OperationDefinition, Integer> maximumOperations;

	/**
	 * Constructor.
	 * 
	 * @param totalOperations
	 * @param totalSpace
	 * @param numberOfDoubles
	 * @param numberOfIntegers
	 * @param maximumOperations
	 */
	public Filter(Integer totalOperations, Integer totalSpace, Integer numberOfDoubles, Integer numberOfIntegers,
			Map<OperationDefinition, Integer> maximumOperations) {
		this.totalOperations = totalOperations;
		this.totalSpace = totalSpace;
		this.numberOfDoubles = numberOfDoubles;
		this.numberOfIntegers = numberOfIntegers;
		this.maximumOperations = maximumOperations;
	}

	/**
	 * Returns the number of total operations.
	 * 
	 * @return
	 */
	public Integer getTotalOperations() {
		return totalOperations;
	}

	/**
	 * Return the total space.
	 * 
	 * @return
	 */
	public Integer getTotalSpace() {
		return totalSpace;
	}

	/**
	 * Returns the number of doubles.
	 * 
	 * @return
	 */
	public Integer getNumberOfDoubles() {
		return numberOfDoubles;
	}

	/**
	 * Returns the number of integers.
	 * 
	 * @return
	 */
	public Integer getNumberOfIntegers() {
		return numberOfIntegers;
	}

	/**
	 * Returns the number of operations.
	 * 
	 * @return
	 */
	public Map<OperationDefinition, Integer> getMaximumOperations() {
		return maximumOperations;
	}

}
