package ecst.io;

import java.io.File;
import java.util.List;

import ecst.algorithm.WorkPhaseAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.combiner.CombinerOutputModel;
import ecst.utilities.FileUtilities;

/**
 * Exports information of the trained classification system that can be used for
 * an automatic implementation of the system on an embedded device. The file is
 * saved as a XML document.
 * 
 * @author Matthias Ring
 * 
 */
public class ClassificationSystemExport {

	/**
	 * Exports all classification systems in the given list into the given file.
	 * 
	 * @param list
	 * @param file
	 * @throws Exception
	 */
	public static void exportXML(List<CombinerOutputModel> list, File file) throws Exception {
		StringBuilder builder = new StringBuilder();

		builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		builder.append("<classificationSystems>\n");
		for (CombinerOutputModel model : list) {
			createModelTag(model, builder);
		}
		builder.append("</classificationSystems>\n");

		FileUtilities.saveUTF8String(builder.toString(), file);
	}

	/**
	 * Exports the given classification system (CombinerOutputModel).
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createModelTag(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<classificationSystem>\n");
		createFeaturesTag(model, builder);
		createPreprocessingTag(model, builder);
		createClassificationTag(model, builder);
		builder.append("</classificationSystem>\n");
	}

	/**
	 * Exports information of the feature selection.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createFeaturesTag(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<features>\n");
		if (model.getFeatureExtractionModel() == null) {
			for (String attribute : model.getData().getAttributesAfterTraining()) {
				builder.append("<feature name=\"");
				builder.append(FileUtilities.exportXMLString(attribute) + "\"/>\n");
			}
		}
		builder.append("</features>\n");
	}

	/**
	 * Exports the user settings and the settings that were determined in the
	 * training phase.
	 * 
	 * @param model
	 * @param builder
	 * @param algorithm
	 */
	private static void createAlgorithmTag(CombinerOutputModel model, StringBuilder builder, WorkPhaseAlgorithm algorithm) {
		//no preprocessing step chosen 
		if (algorithm == null) {
			builder.append("NONE\">\n");		
		}
		else {
			builder.append(FileUtilities.exportXMLString(algorithm.getDefinition().getName()) + "\">\n");
			createUserDefinedParametersTag(builder, algorithm.getParameters());
			//TODO: this has to be tested for all classifiers. Does e.g. not work for AdaBoost
			builder.append("<trainingParameters>\n");
			builder.append(algorithm.modelToXML(model.getData()));
			builder.append("</trainingParameters>\n");
		}
	}

	/**
	 * Exports the employed preprocessing algorithm.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createPreprocessingTag(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<preprocessing name=\"");
		createAlgorithmTag(model, builder, model.getPreprocessingAlgorithm());
		builder.append("</preprocessing>\n");
	}

	/**
	 * Exports the employed classification algorithm.
	 * 
	 * @param model
	 * @param builder
	 */
	private static void createClassificationTag(CombinerOutputModel model, StringBuilder builder) {
		builder.append("<classification name=\"");
		createAlgorithmTag(model, builder, model.getClassificationAlgorithm());
		builder.append("</classification>\n");
	}

	/**
	 * Exports the settings that the user has made in the GUI.
	 * 
	 * @param builder
	 * @param parameters
	 */
	private static void createUserDefinedParametersTag(StringBuilder builder, Parameter[] parameters) {
		builder.append("<userDefinedParameters>\n");
		for (Parameter parameter : parameters) {
			builder.append("<parameter name=\"");
			builder.append(FileUtilities.exportXMLString(parameter.getName()));
			builder.append("\">");
			if (parameter.getType().equals(Parameter.TYPE.SELECTED_PARAMETER)) {
				builder.append(FileUtilities.exportXMLString(""
						+ ((SelectedParameter) parameter.getValue()).getItems().get(((SelectedParameter) parameter.getValue()).getSelectedIndex())
								.getDisplayName()));
			} else {
				builder.append(FileUtilities.exportXMLString("" + parameter.getValue()));
			}
			builder.append("</parameter>\n");
		}
		builder.append("</userDefinedParameters>\n");
	}
}
