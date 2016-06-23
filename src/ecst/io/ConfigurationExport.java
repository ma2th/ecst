package ecst.io;

import java.io.File;
import java.util.List;

import ecst.algorithm.Algorithm;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.featureextraction.FeatureExtractionModel;
import ecst.featureextraction.InputFile;
import ecst.utilities.FileUtilities;
import ecst.view.ECST;

/**
 * Exports the configuration of the GUI as a XML document. The exported XML
 * document can be import with the class ConfigurationImport.
 * 
 * @author Matthias Ring
 * 
 */
public class ConfigurationExport {

	/**
	 * Exports the complete GUI settings.
	 * 
	 * @param file
	 * @param filename
	 * @param featureExtraction
	 * @param featureExtractionModel
	 * @param featureExtractionAlgorithms
	 * @param preprocessingAlgorithms
	 * @param featureSelectionAlgorithms
	 * @param classificationAlgorithms
	 * @param evaluationAlgorithms
	 * @param selectedPreprocessingAlgorithms
	 * @param selectedFeatureSelectionAlgorithms
	 * @param selectedClassificationAlgorithms
	 * @param selectedEvaluationAlgorithms
	 * @throws Exception
	 */
	public static void exportXML(File file, String filename, boolean featureExtraction, FeatureExtractionModel featureExtractionModel,
			List<Algorithm> featureExtractionAlgorithms, List<Algorithm> preprocessingAlgorithms, List<Algorithm> featureSelectionAlgorithms,
			List<Algorithm> classificationAlgorithms, List<Algorithm> evaluationAlgorithms, List<Algorithm> selectedPreprocessingAlgorithms,
			List<Algorithm> selectedFeatureSelectionAlgorithms, List<Algorithm> selectedClassificationAlgorithms, List<Algorithm> selectedEvaluationAlgorithms)
			throws Exception {
		StringBuilder builder = new StringBuilder();

		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("<ecst version=\"");
		builder.append(ECST.VERSION);
		builder.append("\">\n");
		createSourceTag(builder, featureExtraction, filename, featureExtractionModel);
		createAlgorithmsTag(builder, featureExtractionAlgorithms, null, "featureExtraction");
		createAlgorithmsTag(builder, preprocessingAlgorithms, selectedPreprocessingAlgorithms, "preprocessing");
		createAlgorithmsTag(builder, featureSelectionAlgorithms, selectedFeatureSelectionAlgorithms, "featureSelection");
		createAlgorithmsTag(builder, classificationAlgorithms, selectedClassificationAlgorithms, "classification");
		createAlgorithmsTag(builder, evaluationAlgorithms, selectedEvaluationAlgorithms, "evaluation");
		builder.append("</ecst>\n");

		FileUtilities.saveUTF8String(builder.toString(), file);
	}

	/**
	 * Exports the settings of one algorithm parameter.
	 * 
	 * @param builder
	 * @param parameter
	 */
	private static void createParamterTag(StringBuilder builder, Parameter parameter) {
		builder.append("<parameter name=\"");
		builder.append(FileUtilities.exportXMLString(parameter.getName()));
		builder.append("\">");
		if (parameter.getType().equals(Parameter.TYPE.SELECTED_PARAMETER)) {
			builder.append(FileUtilities.exportXMLString("" + ((SelectedParameter) parameter.getValue()).getSelectedIndex()));
		} else {
			builder.append(FileUtilities.exportXMLString("" + parameter.getValue()));
		}
		builder.append("</parameter>\n");
	}

	/**
	 * Exports the settings of one algorithm.
	 * 
	 * @param builder
	 * @param algorithm
	 * @param selected
	 */
	private static void createAlgorithmTag(StringBuilder builder, Algorithm algorithm, List<Algorithm> selected) {
		algorithm.readEditorSettings();

		builder.append("<algorithm class=\"");
		builder.append(FileUtilities.exportXMLString(algorithm.getDefinition().getClassName()));
		builder.append("\" selected=\"");
		if (selected == null) {
			builder.append("true");
		} else {
			builder.append(selected.contains(algorithm));
		}
		builder.append("\">\n");
		if (algorithm.getParameters() != null) {
			for (Parameter parameter : algorithm.getParameters()) {
				createParamterTag(builder, parameter);
			}
		}
		if (algorithm instanceof ClassificationAlgorithm) {
			if (((ClassificationAlgorithm) algorithm).isGridSearch()) {
				createParamterTag(builder, ((ClassificationAlgorithm) algorithm).getEnableGridSearchParameter());
				for (Parameter parameter : ((ClassificationAlgorithm) algorithm).getGridSearchManagerParameter()) {
					createParamterTag(builder, parameter);
				}
			}
		}
		builder.append("</algorithm>\n");
	}

	/**
	 * Exports the settings of all algorithms in the given list.
	 * 
	 * @param builder
	 * @param algorithms
	 * @param selected
	 * @param tagName
	 */
	private static void createAlgorithmsTag(StringBuilder builder, List<Algorithm> algorithms, List<Algorithm> selected, String tagName) {
		if (algorithms.size() == 0) {
			builder.append("<");
			builder.append(FileUtilities.exportXMLString(tagName));
			builder.append("/>\n");
		} else {
			builder.append("<");
			builder.append(FileUtilities.exportXMLString(tagName));
			builder.append(">\n");

			for (Algorithm algorithm : algorithms) {
				if (algorithm != null) {
					createAlgorithmTag(builder, algorithm, selected);
				} else if (selected != null && selected.contains(null)) {
					builder.append("<algorithm class=\"null\" selected=\"true\"/>\n");
				} else {
					builder.append("<algorithm class=\"null\" selected=\"false\"/>\n");
				}
			}

			builder.append("</");
			builder.append(FileUtilities.exportXMLString(tagName));
			builder.append(">\n");
		}
	}

	/**
	 * Exports information on the used source files.
	 * 
	 * @param builder
	 * @param featureExtraction
	 * @param filename
	 * @param featureExtractionModel
	 */
	private static void createSourceTag(StringBuilder builder, boolean featureExtraction, String filename, FeatureExtractionModel featureExtractionModel) {
		builder.append("<source featureExtraction=\"");
		builder.append(featureExtraction);
		builder.append("\">\n");
		builder.append("<featureFile>");
		builder.append(FileUtilities.exportXMLString(filename));
		builder.append("</featureFile>\n");
		createFeatureExtractionTag(builder, featureExtractionModel);
		builder.append("</source>\n");
	}

	/**
	 * Exports information on the feature extraction step.
	 * 
	 * @param builder
	 * @param featureExtractionModel
	 */
	private static void createFeatureExtractionTag(StringBuilder builder, FeatureExtractionModel featureExtractionModel) {
		builder.append("<inputLinesForOneFeature>");
		builder.append(FileUtilities.exportXMLString("" + featureExtractionModel.getInputLinesForOneFeature()));
		builder.append("</inputLinesForOneFeature>\n");
		builder.append("<inputFileType>"); // type before the file list to know
											// the type when importing the xml
											// file
		builder.append(FileUtilities.exportXMLString("" + featureExtractionModel.getInputFileType()));
		builder.append("</inputFileType>\n");
		builder.append("<outputFile>");
		builder.append(FileUtilities.exportXMLString(featureExtractionModel.getOutputFile()));
		builder.append("</outputFile>\n");
		builder.append("<inputFiles>\n");
		for (InputFile inputFile : featureExtractionModel.getInputFiles()) {
			builder.append("<file class=\"");
			builder.append(FileUtilities.exportXMLString(inputFile.getClassLabel()));
			builder.append("\" subjectID=\"");
			builder.append(inputFile.getSubjectID());
			builder.append("\">");
			builder.append(FileUtilities.exportXMLString(inputFile.getFilename()));
			builder.append("</file>\n");
		}
		builder.append("</inputFiles>\n");
	}
}
