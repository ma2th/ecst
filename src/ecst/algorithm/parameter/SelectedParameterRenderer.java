package ecst.algorithm.parameter;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 * ListCellRenderer to display SelectedParameterItems in a list or dropdown box.
 * 
 * @author Matthias Ring
 * 
 */
public class SelectedParameterRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 */
	public SelectedParameterRenderer() {
		setOpaque(true);
	}

	/**
	 * Returns a label containing the display name of the selected item.
	 */
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		SelectedParameterItem entry = (SelectedParameterItem) value;

		if (isSelected) {
			setBackground(list.getSelectionBackground());
			setForeground(list.getSelectionForeground());
		} else {
			setBackground(list.getBackground());
			setForeground(list.getForeground());
		}

		setText("<html>" + entry.getDisplayName() + "</html>");

		return this;
	}

}
