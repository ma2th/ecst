package ecst.algorithm.parameter;

/**
 * This class represents a parameter of an algorithm. The type of parameter can
 * be dynamically changed.
 * 
 * @author Matthias Ring
 * 
 */
public class Parameter implements Comparable<Parameter> {

	/**
	 * All possible parameter types that can be selected
	 */
	public static enum TYPE {
		BOOLEAN, INTEGER, LONG, DOUBLE, STRING, FILE_NAME, SELECTED_PARAMETER
	};

	private String name;
	private Object value;
	private TYPE type;
	private String optionString;
	private String gridSearchString;
	private SelectedParameterItem linkedToSelectedParameterItem;

	public Parameter(Object value, String name, TYPE type, String optionString) {
		this(value, name, type, optionString, null, null);
	}

	public Parameter(Object value, String name, TYPE type, String optionString, String gridSearchString) {
		this(value, name, type, optionString, gridSearchString, null);
	}
	
	public Parameter(Object value, String name, TYPE type, String optionString, String gridSearchString, SelectedParameterItem linkedToSelectedParameterItem) {
		setName(name);
		setType(type);
		setValue(value);
		setOptionString(optionString);
		setGridSearchName(gridSearchString);
		setLinkedToSelectedParameterItem(linkedToSelectedParameterItem);
	}

	/**
	 * Returns the parameter's name.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * Sets the parameter's name.
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the parameter's type.
	 * 
	 * @return
	 */
	public TYPE getType() {
		return type;
	}

	/**
	 * Sets the parameter's type.
	 * 
	 * @param type
	 */
	public void setType(TYPE type) {
		this.type = type;
	}

	/**
	 * Returns the string for the command line version of this algorithm.
	 * 
	 * @return
	 */
	public String getOptionString() {
		return optionString;
	}

	/**
	 * Sets the string for the command line version of this algorithm.
	 * 
	 * @param optionString
	 */
	public void setOptionString(String optionString) {
		this.optionString = optionString;
	}

	/**
	 * Returns the canonical name of the variable that contains the grid search result.
	 * @return
	 */
	public String getGridSearchString() {
		return gridSearchString;
	}

	/**
	 * Sets the canonical name of the variable that contains the grid search result.
	 * @return
	 */
	public void setGridSearchName(String gridSearchString) {
		this.gridSearchString = gridSearchString;
	}

	/**
	 * Returns the value of this parameter.
	 * Note that the value has to be casted according to the specified parameter type.
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Sets the value of this parameter.
	 * @param value
	 */
	public void setValue(Object value) {
		if (type == null) {
			throw new IllegalArgumentException("Set the correct type before setting the value!");
		}
		if (value == null) {
			this.value = value;
			return;
		}

		if ((type == TYPE.DOUBLE && value instanceof Double) || (type == TYPE.SELECTED_PARAMETER && value instanceof SelectedParameter)
				|| (type == TYPE.INTEGER && value instanceof Integer) || (type == TYPE.STRING && value instanceof String)
				|| (type == TYPE.BOOLEAN && value instanceof Boolean) || (type == TYPE.LONG && value instanceof Long)
				|| (type == TYPE.FILE_NAME && value instanceof String)) {
			this.value = value;
			return;
		}
	}

	/**
	 * Returns a string representation of this parameter.
	 */
	@Override
	public String toString() {
		return name + ": " + optionString + " " + value + " (" + type + ")";
	}

	/**
	 * Compares this paramter to another by using the name.
	 */
	@Override
	public int compareTo(Parameter o) {
		return name.compareTo(o.getName());
	}

	public SelectedParameterItem getLinkedToSelectedParameterItem() {
		return linkedToSelectedParameterItem;
	}

	public void setLinkedToSelectedParameterItem(SelectedParameterItem linkedToSelectedParameterItem) {
		this.linkedToSelectedParameterItem = linkedToSelectedParameterItem;
	}

}
