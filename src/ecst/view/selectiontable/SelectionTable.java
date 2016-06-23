package ecst.view.selectiontable;

import java.util.List;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ecst.algorithm.Algorithm;

/**
 * A table that shows a JCheckbox and a string.
 * 
 * @author Matthias Ring
 * 
 */
public class SelectionTable extends JTable {

	private static final long serialVersionUID = 1L;

	private SelectionTableModel model;

	/**
	 * Constructor.
	 */
	public SelectionTable() {
	}

	/**
	 * Initializes the GUI.
	 * 
	 * @param allAlgorithms
	 * @throws Exception
	 */
	public void init(List<List<Algorithm>> allAlgorithms) throws Exception {
		model = new SelectionTableModel(allAlgorithms);
		setModel(model);

		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setTableHeader(null);
		setFillsViewportHeight(true);
		getColumnModel().getColumn(0).setPreferredWidth(25);
		getColumnModel().getColumn(0).setMaxWidth(25);
		setShowGrid(false);
		getColumnModel().setColumnMargin(0);
		setRowMargin(5);
		setRowHeight(23);
	}

	/**
	 * Returns the TableModel.
	 */
	public SelectionTableModel getModel() {
		return model;
	}

	/**
	 * Selects the first row.
	 */
	public void setFirstRowSelected() {
		if (model.getRowCount() > 0) {
			setRowSelectionInterval(0, 0);
			for (ListSelectionListener listener : (ListSelectionListener[]) getListeners(ListSelectionListener.class)) {
				listener.valueChanged(new ListSelectionEvent(this, 0, 0, false));
			}
		}
	}
}
