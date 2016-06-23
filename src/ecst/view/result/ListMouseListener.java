package ecst.view.result;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JList;

import ecst.combiner.CombinerOutputModel;
import ecst.view.ECST;

/**
 * A mouse listener to capture clicks on the result list and to show the
 * DetailsDialog.
 * 
 * @author Matthias Ring
 * 
 */
public class ListMouseListener implements MouseListener {

	private JList list;
	private ECST ecst;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @param list
	 */
	public ListMouseListener(ECST ecst, JList list) {
		this.list = list;
		this.ecst = ecst;
	}

	/**
	 * Mouse clicked event -> show DetailsDialog.
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		DetailsDialog dialog = null;
		CombinerOutputModel model = null;

		if (e.getClickCount() == 2 && list.getSelectedValue() != null) {
			model = (CombinerOutputModel) list.getSelectedValue();
			dialog = new DetailsDialog(ecst, model);
			dialog.setVisible(true);
		}
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseExited(MouseEvent e) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void mousePressed(MouseEvent e) {
	}

	/**
	 * Unused.
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
	}

}
