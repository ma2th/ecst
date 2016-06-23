package ecst.featureextraction;

import java.util.List;

/**
 * A class decribing the settings of the feature extraction step. This class
 * sumamrizes the user settings for feature extraction.
 * 
 * @author Matthias Ring
 * 
 */
public class FeatureExtractionModel {

	public enum INPUT_FILE_TYPE {
		CSV, TAB
	};

	private Integer lines;
	private String outputFile;
	private List<InputFile> inputFiles;
	private INPUT_FILE_TYPE inputFileType;

	/**
	 * Constructor.
	 * 
	 * @param lines
	 * @param outputFile
	 * @param files
	 * @param inputType
	 */
	public FeatureExtractionModel(Integer lines, String outputFile, List<InputFile> files, INPUT_FILE_TYPE inputType) {
		this.lines = lines;
		this.inputFiles = files;
		this.outputFile = outputFile;
		this.inputFileType = inputType;
	}

	/**
	 * Returns the number of lines used for one feature computation.
	 * 
	 * @return
	 */
	public Integer getInputLinesForOneFeature() {
		return lines;
	}

	/**
	 * Sets the number of lines used for one feature computation.
	 * 
	 * @param lines
	 */
	public void setInputLinesForOneFeature(Integer lines) {
		this.lines = lines;
	}

	/**
	 * Returns the name of the output file where the extracted feature should be
	 * saved.
	 * 
	 * @return
	 */
	public String getOutputFile() {
		return outputFile;
	}

	/**
	 * Returns the source files for feature extraction.
	 * 
	 * @return
	 */
	public List<InputFile> getInputFiles() {
		return inputFiles;
	}

	/**
	 * Returns the file type of the source files for feature extraction.
	 * 
	 * @return
	 */
	public INPUT_FILE_TYPE getInputFileType() {
		return inputFileType;
	}

	/**
	 * Returns the delimiter that separates the different signals in the source
	 * files.
	 * 
	 * @return
	 */
	public String getDelimiter() {
		return convertInputType(inputFileType);
	}

	/**
	 * Comfort method to convert the INPUT_FILE_TYPE to a string.
	 * 
	 * @param type
	 * @return
	 */
	public static String convertInputType(INPUT_FILE_TYPE type) {
		if (type.equals(INPUT_FILE_TYPE.CSV)) {
			return ",";
		} else {
			return "\t";
		}
	}

}
