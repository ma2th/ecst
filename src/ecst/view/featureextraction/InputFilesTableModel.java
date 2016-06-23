package ecst.view.featureextraction;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.table.AbstractTableModel;

import ecst.featureextraction.InputFile;

/**
 * The TableModel for the input files table.
 * 
 * @author Matthias Ring
 * 
 */
public class InputFilesTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private String[] columnNames;
	private List<InputFile> inputFileList;

	/**
	 * Constructor.
	 */
	public InputFilesTableModel() {
		columnNames = new String[] { "File name", "Subject ID", "Class label" };
		inputFileList = new LinkedList<InputFile>();
	}

	/**
	 * Adds a new file to the model.
	 * 
	 * @param inputFile
	 */
	public void addInputFile(InputFile inputFile) {
		inputFileList.add(inputFile);
		fireTableDataChanged();
	}

	/**
	 * Removes a file from the model.
	 * 
	 * @param index
	 */
	public void removeInputFile(int index) {
		inputFileList.remove(index);
		fireTableDataChanged();
	}

	/**
	 * Returns all files.
	 * 
	 * @return
	 */
	public List<InputFile> getInputFiles() {
		return inputFileList;
	}

	/**
	 * Sets all files.
	 * 
	 * @param inputFileList
	 */
	public void setInputFiles(List<InputFile> inputFileList) {
		this.inputFileList = inputFileList;
		fireTableDataChanged();
	}

	/**
	 * Returns a Set of the class labels.
	 * 
	 * @return
	 */
	public Set<String> getClassLabels() {
		Set<String> classLabels = new HashSet<String>();

		for (InputFile inputFile : inputFileList) {
			classLabels.add(inputFile.getClassLabel());
		}
		return classLabels;
	}

	/**
	 * Returns a Set of the subject IDs.
	 * 
	 * @return
	 */
	public Set<Integer> getSubjectIDs() {
		Set<Integer> subjectIDs = new HashSet<Integer>();

		for (InputFile inputFile : inputFileList) {
			subjectIDs.add(inputFile.getSubjectID());
		}
		return subjectIDs;
	}

	/**
	 * Sets the class labels.
	 * 
	 * @param indices
	 * @param classLabel
	 */
	public void setClassLabels(int[] indices, String classLabel) {
		for (int i : indices) {
			inputFileList.get(i).setClassLabel(classLabel);
		}
		fireTableDataChanged();
	}

	/**
	 * Sets the subject IDs.
	 * 
	 * @param indices
	 * @param subjectID
	 */
	public void setSubjectIDs(int[] indices, Integer subjectID) {
		for (int i : indices) {
			inputFileList.get(i).setSubjectID(subjectID);
		}
		fireTableDataChanged();
	}

	/**
	 * Returns the column name.
	 */
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
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
		return inputFileList.size();
	}

	/**
	 * Returns if the cell is editable.
	 */
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		if (columnIndex == 0) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * Returns the element at the given position.
	 */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		InputFile inputFile = inputFileList.get(rowIndex);

		if (columnIndex == 0) {
			return inputFile.getFilename().substring(1 + inputFile.getFilename().lastIndexOf(System.getProperty("file.separator")));
		} else if (columnIndex == 1) {
			return inputFile.getSubjectID();
		} else {
			return inputFile.getClassLabel();
		}
	}

	/**
	 * Sets the element at the given position.
	 */
	@Override
	public void setValueAt(Object value, int rowIndex, int columnIndex) {
		String text = (String) value;
		InputFile inputFile = inputFileList.get(rowIndex);

		if (columnIndex == 2) {
			inputFile.setClassLabel(text);
		} else if (columnIndex == 1) {
			inputFile.setSubjectID(Integer.parseInt(text));
		}
	}

}
