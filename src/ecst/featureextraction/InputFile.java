package ecst.featureextraction;

import java.io.File;

/**
 * A file representing the content of the table in the feature extraction tab.
 * Every line in the table contains three entries: filename, subject id and
 * class label.
 * 
 * @author Matthias Ring
 * 
 */
public class InputFile {

	private String filename;
	private String classLabel;
	private Integer subjectID;

	/**
	 * Constructor. Extract an proposal for the class label based on the
	 * filename.
	 * 
	 * @param filename
	 */
	public InputFile(String filename) {
		int index = 0;
		String[] splits;
		File tmpFile = new File(filename);

		index = tmpFile.getName().lastIndexOf(".");
		if (index == -1) {
			index = tmpFile.getName().length();
		}
		classLabel = tmpFile.getName().substring(0, index);
		splits = classLabel.split("\\d");
		classLabel = splits[0];

		this.filename = filename;
	}

	/**
	 * Returns the filename.
	 * 
	 * @return
	 */
	public String getFilename() {
		return filename;
	}

	/**
	 * Returns the class label.
	 * 
	 * @return
	 */
	public String getClassLabel() {
		return classLabel;
	}

	/**
	 * Sets the filename.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		this.filename = filename;
	}

	/**
	 * Sets the class label.
	 * 
	 * @param classLabel
	 */
	public void setClassLabel(String classLabel) {
		this.classLabel = classLabel;
	}

	/**
	 * Returns the subject ID.
	 * 
	 * @return
	 */
	public Integer getSubjectID() {
		return subjectID;
	}

	/**
	 * Sets the subject ID.
	 * 
	 * @param subjectID
	 */
	public void setSubjectID(Integer subjectID) {
		this.subjectID = subjectID;
	}

}
