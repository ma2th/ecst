package ecst.io;

import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import ecst.algorithm.analysis.Analysis;
import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.OperationDefinition;
import ecst.combiner.CombinerOutputModel;
import ecst.featureextraction.InputFile;
import ecst.utilities.CommonUtilities;
import ecst.utilities.FileUtilities;
import ecst.utilities.MathUtilities;
import weka.core.Attribute;

/**
 * This class exports the complexity analysis to a XML document.
 * 
 * @author Matthias Ring
 * 
 */
public class AnalysisExport {

	/**
	 * Exports the given list of CombinerOutputModel objects into the given
	 * file.
	 * 
	 * @param list
	 * @param file
	 * @throws Exception
	 */
	public static void exportXML(List<CombinerOutputModel> list, File file) throws Exception {
		StringBuilder builder = new StringBuilder();

		createHeaderXML(builder);
		for (CombinerOutputModel model : list) {
			createModelXML(model, builder);
		}
		builder.append("</complexityAnalysis>\n");

		FileUtilities.saveUTF8String(builder.toString(), file);
	}

	public static void exportLatex(List<CombinerOutputModel> list, File file) throws Exception {
		StringBuilder builder = new StringBuilder();

		createHeaderLatex(builder);
		for (CombinerOutputModel model : list) {
			createModelLatex(model, builder);
			builder.append("\n\n");
		}
		builder.append("\\end{document}\n");

		FileUtilities.saveUTF8String(builder.toString(), file);
	}

	public static void exportCSV(List<CombinerOutputModel> list, File file) throws Exception {
		StringBuilder builder = new StringBuilder();

		createHeaderCSV(builder);
		for (CombinerOutputModel model : list) {
			createModelCSV(model, builder);
			builder.append("\n");
		}

		FileUtilities.saveUTF8String(builder.toString(), file);
	}

	private static void createHeaderXML(StringBuilder builder) {
		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("<complexityAnalysis>\n");
	}

	private static void createHeaderLatex(StringBuilder builder) {
		builder.append("\\documentclass{standalone}\n\n");
		builder.append("\\usepackage{booktabs}\n");
		builder.append("\\usepackage{tabularx}\n");
		builder.append("\\usepackage{ulem}\n\n");
		builder.append("\\newcolumntype{L}{>{\\raggedright\\arraybackslash}X}\n\n");
		builder.append("\\begin{document}\n\n");
	}

	private static void createHeaderCSV(StringBuilder builder) {
		builder.append("Filename");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Preprocessing");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Feature selection");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Classification");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Evaluation");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Accuracy (%)");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Instances");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Attributes");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Selected attributes");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Unselected attributes");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Confusion matrix");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Instances correctly classified");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Instances not correctly classified");
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append("Training and evaluation time (ms)");
		builder.append("\n");
	}

	/**
	 * Exports the given CombinerOutputModel.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createModelXML(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<pipelineConfiguration>\n");
		createSourceXML(model, builder);
		createConfigurationXML(model, builder);
		builder.append("<accuracy>");
		builder.append(FileUtilities.exportXMLString("" + model.getEvaluationResult().pctCorrect()));
		builder.append("</accuracy>\n");
		builder.append("<instances>");
		builder.append(FileUtilities.exportXMLString("" + model.getData().getPreprocessedInstances().numInstances()));
		builder.append(" of ");
		builder.append(FileUtilities.exportXMLString("" + model.getData().getInstances().numInstances()));
		builder.append("</instances>\n");
		createAttributesXML(model, builder);
		builder.append("<complexity>\n");
		createSpaceXML(model, builder);
		createOperationsXML(model, builder);
		builder.append("</complexity>\n");
		builder.append("</pipelineConfiguration>\n");
	}

	private static void createModelLatex(CombinerOutputModel model, StringBuilder builder) {
		NumberFormat format = DecimalFormat.getInstance();
		List<OperationDefinition> operationDefinitions = AlgorithmBox.getInstance().getOperationDefinitions();
		int numOperations = operationDefinitions.size();

		builder.append("\\begin{tabularx}{\\textwidth}{ll");
		for (int i = 0; i < numOperations; i++) {
			builder.append("l");
		}
		builder.append("}\n");
		builder.append("\\toprule\n");

		createConfigurationLatex(model, builder, numOperations);
		builder.append("File & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(FileUtilities
				.exportLatexString(model.getFilename().substring(model.getFilename().lastIndexOf(File.separator) + 1)));
		builder.append("} \\\\\n");
		builder.append("Accuracy & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(format.format(model.getEvaluationResult().pctCorrect()));
		builder.append("\\%} \\\\\n");
		builder.append("TP Rate & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(CommonUtilities.createTPRateString(model, format, false));
		builder.append("} \\\\\n");
		builder.append("Instances & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(model.getData().getPreprocessedInstances().numInstances());
		builder.append("/");
		builder.append(model.getData().getInstances().numInstances());
		builder.append("} \\\\\n");
		createAttributesLatex(model, builder, numOperations);
		createSpaceLatex(model, builder, numOperations);
		createOperationsLatex(model, builder);

		builder.append("\\bottomrule\n");
		builder.append("\\end{tabularx}\n");
	}

	private static void createModelCSV(CombinerOutputModel model, StringBuilder builder) {
		NumberFormat decimalFormat = DecimalFormat.getInstance();
		NumberFormat integerFormat = NumberFormat.getIntegerInstance();

		builder.append(FileUtilities.exportCSVString(model.getFilename()));
		builder.append(FileUtilities.CSV_DELIMITER);

		createConfigurationCSV(model, builder);

		builder.append(FileUtilities.exportCSVString(decimalFormat.format(model.getEvaluationResult().pctCorrect())));
		builder.append(FileUtilities.CSV_DELIMITER);

		builder.append(FileUtilities.exportCSVString("" + model.getData().getPreprocessedInstances().numInstances()));
		builder.append("/");
		builder.append(FileUtilities.exportCSVString("" + model.getData().getInstances().numInstances()));
		builder.append(FileUtilities.CSV_DELIMITER);

		createAttributesCSV(model, builder);

		createConfusionMatrixCSV(model, builder);
		
		builder.append(FileUtilities.exportCSVString(integerFormat.format(model.getEvaluationResult().correct())));
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append(FileUtilities.exportCSVString(integerFormat.format(model.getEvaluationResult().incorrect())));
		builder.append(FileUtilities.CSV_DELIMITER);
		
		builder.append(FileUtilities.exportCSVString("" + model.getTime()));
	}

	private static void createConfusionMatrixCSV(CombinerOutputModel model, StringBuilder builder) {
		String confusionString = null;
		double[][] confusionMatrix = null;

		confusionMatrix = model.getEvaluationResult().confusionMatrix();
		confusionString = "[ ";
		for (int i = 0; i < confusionMatrix.length; i++) {
			for (int j = 0; j < confusionMatrix[i].length; j++) {
				if (j > 0) {
					confusionString =  confusionString + ", " + confusionMatrix[i][j];
				} else {
					confusionString = confusionString + confusionMatrix[i][j];
				}
			}
			if (i < confusionMatrix.length - 1) {
				confusionString += "; ";
			}
		}
		confusionString += " ]";
		
		builder.append(FileUtilities.exportCSVString(confusionString));
		builder.append(FileUtilities.CSV_DELIMITER);
	}

	private static void createConfigurationCSV(CombinerOutputModel model, StringBuilder builder) {
		if (model.getPreprocessingAlgorithm() != null) {
			builder.append(FileUtilities.exportCSVString(
					CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getPreprocessingAlgorithm(), false)));
		}
		builder.append(FileUtilities.CSV_DELIMITER);
		if (model.getFeatureSelectionAlgorithm() != null) {
			builder.append(FileUtilities.exportCSVString(CommonUtilities
					.createAlgorithmNameWithInstanceCounter(model.getFeatureSelectionAlgorithm(), false)));
		}
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append(FileUtilities.exportCSVString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getClassificationAlgorithm(), false)));
		builder.append(FileUtilities.CSV_DELIMITER);
		builder.append(FileUtilities.exportCSVString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getEvaluationAlgorithm(), false)));
		builder.append(FileUtilities.CSV_DELIMITER);
	}

	/**
	 * Exports the summed space requirements.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createSpaceXML(CombinerOutputModel model, StringBuilder builder) {
		Analysis analysis = new Analysis(model);

		builder.append("<space>\n");
		builder.append("<integer>");
		builder.append(FileUtilities.exportXMLString("" + MathUtilities.sumIntArray(analysis.getNumberOfIntegers())));
		builder.append("</integer>\n");
		builder.append("<float>");
		builder.append(FileUtilities.exportXMLString("" + MathUtilities.sumIntArray(analysis.getNumberOfFloats())));
		builder.append("</float>\n");

		createDetailedSpaceXML(model, builder);

		builder.append("</space>\n");
	}

	private static void createSpaceLatex(CombinerOutputModel model, StringBuilder builder, int numOperations) {
		Analysis analysis = new Analysis(model);

		builder.append("Memory & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(analysis.getTotalSpace());
		builder.append(" parameters: ");
		builder.append(CommonUtilities.createComplexityDetailsStringLatex(analysis.getNumberOfFloats()));
		builder.append(" floating point and ");
		builder.append(CommonUtilities.createComplexityDetailsStringLatex(analysis.getNumberOfIntegers()));
		builder.append(" integer} \\\\\n");
	}

	/**
	 * Exports the space requirements for each pipeline step.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createDetailedSpaceXML(CombinerOutputModel model, StringBuilder builder) {
		Analysis analysis = new Analysis(model);

		builder.append("<detailedSpace>\n");

		for (int i = 0; i < 3; i++) {
			// tag the pipeline step
			switch (i) {
			case 0:
				builder.append("<featureextraction>\n");
				break;
			case 1:
				builder.append("<preprocessing>\n");
				break;
			case 2:
				builder.append("<classification>\n");
				break;
			}

			builder.append("<integer>");
			builder.append(FileUtilities.exportXMLString("" + (analysis.getNumberOfIntegers())[i]));
			builder.append("</integer>\n");
			builder.append("<float>");
			builder.append(FileUtilities.exportXMLString("" + (analysis.getNumberOfFloats())[i]));
			builder.append("</float>\n");

			switch (i) {
			case 0:
				builder.append("</featureextraction>\n");
				break;
			case 1:
				builder.append("</preprocessing>\n");
				break;
			case 2:
				builder.append("</classification>\n");
				break;
			}
		}

		builder.append("</detailedSpace>\n");
	}

	/**
	 * Exports the summed operation requirements.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createOperationsXML(CombinerOutputModel model, StringBuilder builder) {
		Analysis analysis = new Analysis(model);
		List<OperationDefinition> operationDefinitions = AlgorithmBox.getInstance().getOperationDefinitions();

		builder.append("<operations>\n");
		for (OperationDefinition definition : operationDefinitions) {
			builder.append("<");
			builder.append(FileUtilities.exportXMLString(definition.getName()));
			builder.append(">");
			builder.append(
					FileUtilities.exportXMLString("" + MathUtilities.sumIntArray(analysis.getOperations(definition))));
			builder.append("</");
			builder.append(FileUtilities.exportXMLString(definition.getName()));
			builder.append(">\n");
		}
		createDetailedOperationsXML(model, builder);
		builder.append("</operations>\n");
	}

	private static void createOperationsLatex(CombinerOutputModel model, StringBuilder builder) {
		int[] sum;
		String result;
		Analysis analysis = new Analysis(model);
		List<OperationDefinition> operationDefinitions = AlgorithmBox.getInstance().getOperationDefinitions();

		builder.append("Operations & ");
		for (int i = 0; i < operationDefinitions.size(); i++) {
			builder.append(operationDefinitions.get(i).getDescriptionLatex());
			if (i < operationDefinitions.size() - 1) {
				builder.append(" & ");
			}
		}
		builder.append(" \\\\\n");
		builder.append("\\cmidrule{2-");
		builder.append(operationDefinitions.size() + 1);
		builder.append("}");
		builder.append("{\\hfill(");
		builder.append(analysis.getTotalOperations());
		builder.append(" total)}");
		builder.append(" & ");
		for (int i = 0; i < operationDefinitions.size(); i++) {
			sum = analysis.getOperations(operationDefinitions.get(i));
			result = CommonUtilities.createComplexityDetailsStringLatex(sum);
			builder.append(result);
			if (i < operationDefinitions.size() - 1) {
				builder.append(" & ");
			}
		}
		builder.append(" \\\\\n");
	}

	/**
	 * Exports the operation requirements for each pipeline step.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createDetailedOperationsXML(CombinerOutputModel model, StringBuilder builder) {
		Analysis analysis = new Analysis(model);
		List<OperationDefinition> operationDefinitions = AlgorithmBox.getInstance().getOperationDefinitions();

		builder.append("<detailedOperations>\n");
		for (int i = 0; i < 3; i++) {
			// tag the pipeline step
			switch (i) {
			case 0:
				builder.append("<featureextraction>\n");
				break;
			case 1:
				builder.append("<preprocessing>\n");
				break;
			case 2:
				builder.append("<classification>\n");
				break;
			}

			for (OperationDefinition definition : operationDefinitions) {
				builder.append("<");
				builder.append(FileUtilities.exportXMLString(definition.getName()));
				builder.append(">");
				builder.append(FileUtilities.exportXMLString("" + (analysis.getOperations(definition))[i]));
				builder.append("</");
				builder.append(FileUtilities.exportXMLString(definition.getName()));
				builder.append(">\n");
			}

			switch (i) {
			case 0:
				builder.append("</featureextraction>\n");
				break;
			case 1:
				builder.append("</preprocessing>\n");
				break;
			case 2:
				builder.append("</classification>\n");
				break;
			}
		}
		builder.append("</detailedOperations>\n");
	}

	/**
	 * Exports the result of the feature selection.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createAttributesXML(CombinerOutputModel model, StringBuilder builder) {
		Attribute trainingAttribute = null;

		builder.append("<attributes>\n");
		for (int i = 0; i < model.getData().getInstances().numAttributes() - 1; i++) {
			trainingAttribute = model.getData().getInstances().attribute(i);
			if (model.getData().getInstances().classIndex() == i
					|| model.getData().getAttributesAfterTraining().contains(trainingAttribute.name())) {
				builder.append("<attribute selected=\"true\">");
				builder.append(FileUtilities.exportXMLString(trainingAttribute.name()));
				builder.append("</attribute>\n");
			} else {
				builder.append("<attribute selected=\"false\">");
				builder.append(FileUtilities.exportXMLString(trainingAttribute.name()));
				builder.append("</attribute>\n");
			}
		}
		builder.append("</attributes>\n");
	}

	private static void createAttributesLatex(CombinerOutputModel model, StringBuilder builder, int numOperations) {
		Attribute trainingAttribute = null;

		builder.append("Attributes & \\multicolumn{");
		builder.append(numOperations);
		builder.append("}{L}{");
		builder.append(model.getData().getAttributesAfterTraining().size() + "/"
				+ (model.getData().getInstances().numAttributes() - 1));
		builder.append("; ");
		for (int i = 0; i < model.getData().getInstances().numAttributes() - 1; i++) {
			trainingAttribute = model.getData().getInstances().attribute(i);
			if (model.getData().getInstances().classIndex() == i
					|| model.getData().getAttributesAfterTraining().contains(trainingAttribute.name())) {
				builder.append(FileUtilities.exportLatexString(trainingAttribute.name()));
			} else {
				builder.append("\\sout{");
				builder.append(FileUtilities.exportLatexString(trainingAttribute.name()));
				builder.append("}");
			}
			if (i < model.getData().getInstances().numAttributes() - 2) {
				builder.append(", ");
			}
		}
		builder.append("} \\\\\n");
	}

	private static void createAttributesCSV(CombinerOutputModel model, StringBuilder builder) {
		Attribute trainingAttribute = null;
		String attributes = null;

		builder.append(FileUtilities.exportCSVString(model.getData().getAttributesAfterTraining().size() + "/"
				+ (model.getData().getInstances().numAttributes() - 1)));

		builder.append(FileUtilities.CSV_DELIMITER);

		for (int i = 0; i < model.getData().getInstances().numAttributes() - 1; i++) {
			trainingAttribute = model.getData().getInstances().attribute(i);
			if (model.getData().getInstances().classIndex() == i
					|| model.getData().getAttributesAfterTraining().contains(trainingAttribute.name())) {
				if (attributes == null) {
					attributes = trainingAttribute.name();
				} else {
					attributes += ", " + trainingAttribute.name();
				}
			}
		}
		builder.append(FileUtilities.exportCSVString(attributes));
		builder.append(FileUtilities.CSV_DELIMITER);

		attributes = null;
		for (int i = 0; i < model.getData().getInstances().numAttributes() - 1; i++) {
			trainingAttribute = model.getData().getInstances().attribute(i);
			if (model.getData().getInstances().classIndex() != i
					&& !model.getData().getAttributesAfterTraining().contains(trainingAttribute.name())) {
				if (attributes == null) {
					attributes = trainingAttribute.name();
				} else {
					attributes += ", " + trainingAttribute.name();
				}
			}
		}
		builder.append(FileUtilities.exportCSVString(attributes));
		builder.append(FileUtilities.CSV_DELIMITER);
	}

	/**
	 * Exports the configuration of the classification system.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createConfigurationXML(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<algorithms>\n");
		if (model.getPreprocessingAlgorithm() != null) {
			builder.append("<preprocessing>");
			builder.append(FileUtilities.exportXMLString(
					CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getPreprocessingAlgorithm(), false)));
			builder.append("</preprocessing>\n");
		}
		if (model.getFeatureSelectionAlgorithm() != null) {
			builder.append("<featureSelection>");
			builder.append(FileUtilities.exportXMLString(CommonUtilities
					.createAlgorithmNameWithInstanceCounter(model.getFeatureSelectionAlgorithm(), false)));
			builder.append("</featureSelection>\n");
		}
		builder.append("<classification>");
		builder.append(FileUtilities.exportXMLString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getClassificationAlgorithm(), false)));
		builder.append("</classification>\n");
		builder.append("<evaluation>");
		builder.append(FileUtilities.exportXMLString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getEvaluationAlgorithm(), false)));
		builder.append("</evaluation>\n");
		builder.append("</algorithms>\n");
	}

	private static void createConfigurationLatex(CombinerOutputModel model, StringBuilder builder, int numOperations) {
		builder.append("\\multicolumn{");
		builder.append(1 + numOperations);
		builder.append("}{L}{");
		if (model.getPreprocessingAlgorithm() != null) {
			builder.append(FileUtilities.exportLatexString(
					CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getPreprocessingAlgorithm(), false)));
			builder.append(" $\\rightarrow$ ");
		}
		if (model.getFeatureSelectionAlgorithm() != null) {
			builder.append(FileUtilities.exportLatexString(CommonUtilities
					.createAlgorithmNameWithInstanceCounter(model.getFeatureSelectionAlgorithm(), false)));
			builder.append(" $\\rightarrow$ ");
		}
		builder.append(FileUtilities.exportLatexString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getClassificationAlgorithm(), false)));
		builder.append(" $\\rightarrow$ ");
		builder.append(FileUtilities.exportLatexString(
				CommonUtilities.createAlgorithmNameWithInstanceCounter(model.getEvaluationAlgorithm(), false)));
		builder.append("} \\\\\n");
		builder.append("\\midrule\n");
	}

	/**
	 * Exports information on the input files of the classification system.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createSourceXML(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<source ");
		if (model.getFeatureExtractionModel() == null) {
			builder.append("featureExtraction=\"false\">\n");
			builder.append("<file>");
			builder.append(FileUtilities.exportXMLString(model.getFilename()));
			builder.append("</file>\n");
		} else {
			builder.append("featureExtraction=\"true\">\n");
			for (InputFile inputFile : model.getFeatureExtractionModel().getInputFiles()) {
				builder.append("<file class=\"");
				builder.append(FileUtilities.exportXMLString(inputFile.getClassLabel()));
				builder.append("\">");
				builder.append(FileUtilities.exportXMLString(inputFile.getFilename()));
				builder.append("</file>\n");
			}
		}
		builder.append("</source>\n");
	}
}
