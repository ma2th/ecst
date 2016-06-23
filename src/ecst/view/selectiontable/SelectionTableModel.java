package ecst.view.selectiontable;

import java.util.LinkedList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ecst.algorithm.Algorithm;

/**
 * A TableModel that manage a JCheckBox and the algorithm name.
 * 
 * @author Matthias Ring
 * 
 */
public class SelectionTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private List<Boolean> selectedAlgorithms;
	private List<List<Algorithm>> allAlgorithms;

	/**
	 * Constructor.
	 * 
	 * @param allAlgorithms
	 * @throws Exception
	 */
	public SelectionTableModel(List<List<Algorithm>> allAlgorithms) throws Exception {
		selectedAlgorithms = new LinkedList<Boolean>();

		for (@SuppressWarnings("unused")
		List<Algorithm> list : allAlgorithms) {
			selectedAlgorithms.add(false);
		}
		this.allAlgorithms = allAlgorithms;
	}

	/**
	 * Returns a list of booleans indicating the selected algorithms.
	 * 
	 * @return
	 */
	public List<Boolean> getSelectedAlgorithms() {
		return selectedAlgorithms;
	}

	/**
	 * Sets the selected algorithms.
	 * 
	 * @param selectedAlgorithms
	 */
	public void setSelectedAlgorithms(List<Boolean> selectedAlgorithms) {
		this.selectedAlgorithms = selectedAlgorithms;
	}

	/**
	 * Returns all algorithms.
	 * 
	 * @return
	 */
	public List<List<Algorithm>> getAllAlgorithms() {
		return allAlgorithms;
	}

	/**
	 * Return the algorithm in the given row.
	 * 
	 * @param row
	 * @return
	 */
	public List<Algorithm> getAlgorithms(int row) {
		return allAlgorithms.get(row);
	}

	/**
	 * Adds an algorithm to this model.
	 * 
	 * @param row
	 * @param number
	 * @throws Exception
	 */
	public void addAlgorithm(int row, int number) throws Exception {
		Algorithm algorithm = null;

		algorithm = allAlgorithms.get(row).get(0).getDefinition().createInstance();
		allAlgorithms.get(row).add(number, algorithm);
		fireTableCellUpdated(row, 1);
	}

	/**
	 * Removes an algorithm from this model.
	 * 
	 * @param row
	 * @param number
	 */
	public void removeAlgorithm(int row, int number) {
		allAlgorithms.get(row).remove(number);
		fireTableCellUpdated(row, 1);
	}

	/**
	 * Returns the column count.
	 */
	@Override
	public int getColumnCount() {
		return 2;
	}

	/**
	 * Returns the row count.
	 */
	@Override
	public int getRowCount() {
		return allAlgorithms.size();
	}

	/**
	 * Returns if the given cell is editable.
	 */
	@Override
	public boolean isCellEditable(int row, int column) {
		if (column == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns the class of the given column.
	 */
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Class getColumnClass(int column) {
		if (column == 0) {
			return Boolean.class;
		} else {
			return String.class;
		}
	}

	/**
	 * Returns the value at the given position.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column == 0) {
			return selectedAlgorithms.get(row);
		} else {
			if (allAlgorithms.get(row) == null) {
				return "None";
			} else {
				return allAlgorithms.get(row).get(0).getDefinition().getName();
			}
		}
	}

	/**
	 * Sets the value at the given position.
	 */
	@Override
	public void setValueAt(Object value, int row, int column) {
		selectedAlgorithms.set(row, (Boolean) value);
		fireTableCellUpdated(row, column);
	}
}
