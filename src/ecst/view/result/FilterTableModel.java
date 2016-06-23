package ecst.view.result;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;
import javax.swing.table.AbstractTableModel;

import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.OperationDefinition;
import ecst.view.ECST;

/**
 * A TableModel for the upper bounds on the operations in the complexity
 * analysis.
 * 
 * @author Matthias Ring
 * 
 */
public class FilterTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private FilterDialog dialog;
	private String[] columnNames;
	private Integer totalOperations;
	private List<OperationDefinition> operations;
	private Map<OperationDefinition, Integer> maximumOperations;

	/**
	 * Constructor.
	 * 
	 * @param dialog
	 */
	public FilterTableModel(FilterDialog dialog) {
		operations = AlgorithmBox.getInstance().getOperationDefinitions();

		maximumOperations = new HashMap<OperationDefinition, Integer>();
		columnNames = new String[operations.size() + 1];
		for (int i = 0; i < operations.size(); i++) {
			columnNames[i] = operations.get(i).getDesciptionHTML();
			maximumOperations.put(operations.get(i), null);
		}
		columnNames[columnNames.length - 1] = "Total";

		this.dialog = dialog;
	}

	/**
	 * Returns the total number of operations.
	 * 
	 * @return
	 */
	public Integer getTotalOperations() {
		return totalOperations;
	}

	/**
	 * Returns the upper bounds on the different operations.
	 * 
	 * @return
	 */
	public Map<OperationDefinition, Integer> getMaximumOperations() {
		return maximumOperations;
	}

	/**
	 * Initializes the table with the given data.
	 * 
	 * @param initOperations
	 * @param totalOperations
	 */
	public void init(Map<OperationDefinition, Integer> initOperations, Integer totalOperations) {
		Integer value = null;

		for (int i = 0; i < columnNames.length - 1; i++) {
			if ((value = initOperations.get(operations.get(i))) != null) {
				setValueAt(value.toString(), 0, i);
			}
		}
		if (totalOperations != null) {
			setValueAt(totalOperations.toString(), 0, getColumnCount() - 1);
		}
	}

	/**
	 * Returns the column name.
	 */
	public String getColumnName(int column) {
		return "<html>" + columnNames[column] + "</html>";
	}

	/**
	 * Returns the column count.
	 */
	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	/**
	 * Returns the row count.
	 */
	@Override
	public int getRowCount() {
		return 1;
	}

	/**
	 * Returns if the cell is editable.
	 */
	public boolean isCellEditable(int row, int column) {
		return true;
	}

	/**
	 * Returns the value at the given position.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		if (column == getColumnCount() - 1) {
			return totalOperations;
		}
		for (OperationDefinition definition : maximumOperations.keySet()) {
			if (definition.equals(operations.get(column))) {
				return maximumOperations.get(definition);
			}
		}
		return null;
	}

	/**
	 * Sets the value at the given position.
	 */
	public void setValueAt(Object value, int row, int column) {
		Integer integer = null;

		if (value == null || ((String) value).equals("")) {
			integer = null;
		} else {
			try {
				integer = Integer.parseInt((String) value);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(dialog, "Please enter an integer!", ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		if (column == getColumnCount() - 1) {
			totalOperations = integer;
		} else {
			for (OperationDefinition definition : maximumOperations.keySet()) {
				if (definition.equals(operations.get(column))) {
					maximumOperations.put(definition, integer);
					break;
				}
			}
		}
		fireTableCellUpdated(row, column);
	}

}
