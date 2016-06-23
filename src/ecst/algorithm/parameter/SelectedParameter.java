package ecst.algorithm.parameter;

import java.util.List;

/**
 * This parameter offers a list of pre-defined values. The user cannot enter
 * values, but only choose one pre-defined value.
 * 
 * @author Matthias Ring
 * 
 */
public class SelectedParameter {

	private int selectedIndex;
	private List<SelectedParameterItem> items;

	/**
	 * Constructor.
	 * @param items the possible values that can be selected
	 * @param selection the current selection
	 */
	public SelectedParameter(List<SelectedParameterItem> items, int selection) {
		this.items = items;
		this.selectedIndex = selection;
	}

	/**
	 * Returns the index of the selected item.
	 * @return
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}

	/**
	 * Sets the index of the selected item.
	 * @param selectedIndex
	 */
	public void setSelectedIndex(int selectedIndex) {
		this.selectedIndex = selectedIndex;
	}

	/**
	 * Returns all items that are available for selection.
	 * @return
	 */
	public List<SelectedParameterItem> getItems() {
		return items;
	}

}
