package ecst.view.result;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import ecst.algorithm.analysis.Analysis;
import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.OperationDefinition;
import ecst.utilities.CommonUtilities;

/**
 * TableModel for the complexity analysis result. Used in the JPanel that is
 * created in ListRenderer class.
 * 
 * @author Matthias Ring
 * 
 */
public class OperationsTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames;
	private List<OperationDefinition> operations;
	private Analysis analysis;

	/**
	 * Constructor. Creates the table headings.
	 */
	public OperationsTableModel() {
		operations = AlgorithmBox.getInstance().getOperationDefinitions();

		columnNames = new String[operations.size()];
		for (int i = 0; i < operations.size(); i++) {
			columnNames[i] = operations.get(i).getDesciptionHTML();
		}
	}

	/**
	 * Sets the data to be displayed.
	 * 
	 * @param analysis
	 */
	public void setTrainedPipeline(Analysis analysis) {
		this.analysis = analysis;
		fireTableDataChanged();
	}

	/**
	 * Returns the column name.
	 */
	@Override
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
		if (analysis == null) {
			return 0;
		}
		return 1;
	}

	/**
	 * Return the value at the given position.
	 */
	@Override
	public Object getValueAt(int row, int column) {
		int[] sum;

		if (analysis == null) {
			return null;
		}

		sum = analysis.getOperations(operations.get(column));
		return "<html>" + CommonUtilities.createComplexityDetailsStringHTML(sum) + "</html>";
	}
}
