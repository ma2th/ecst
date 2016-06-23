package ecst.algorithm.parameter;

/**
 * This class is an item in a SelectedParameter object.
 * 
 * @author Matthias Ring
 * 
 */
public class SelectedParameterItem {

	private String displayName;
	private String optionString;

	/**
	 * Constructor.
	 * 
	 * @param displayName
	 * @param optionString
	 */
	public SelectedParameterItem(String displayName, String optionString) {
		this.displayName = displayName;
		this.optionString = optionString;
	}

	/**
	 * Returns the string describing this item.
	 * 
	 * @return
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Returns the option for the command line version of this item.
	 * 
	 * @return
	 */
	public String getOptionString() {
		return optionString;
	}

}
