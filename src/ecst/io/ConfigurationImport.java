package ecst.io;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ecst.algorithm.Algorithm;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.AlgorithmDefinition;
import ecst.algorithm.parameter.Parameter;
import ecst.featureextraction.FeatureExtractionModel.INPUT_FILE_TYPE;
import ecst.featureextraction.InputFile;
import ecst.utilities.ParameterUtilities;
import ecst.view.FeatureExtractionPanel;
import ecst.view.PipelineStepPanel;

/**
 * This class reads the settings of the complete GUI from a XML document. This
 * XML document can be exported with the class ConfigurationExport.
 * 
 * @author Matthias Ring
 * 
 */
public class ConfigurationImport {

	/**
	 * Reads the entire XML document.
	 * 
	 * @param file
	 * @param featureExtractionPanel
	 * @param preprocessingPanel
	 * @param featureSelectionPanel
	 * @param classificationPanel
	 * @param evaluationPanel
	 * @throws Exception
	 */
	public static void importXML(File file, FeatureExtractionPanel featureExtractionPanel, PipelineStepPanel preprocessingPanel,
			PipelineStepPanel featureSelectionPanel, PipelineStepPanel classificationPanel, PipelineStepPanel evaluationPanel) throws Exception {
		Node root = null;
		Node node = null;
		Document doc = null;
		NodeList nodes = null;

		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
		doc.getDocumentElement().normalize();
		root = doc.getDocumentElement();
		nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("source")) {
				importSource(node, featureExtractionPanel);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("featureExtraction")) {
				importFeatureExtractionAlgorithms(node, featureExtractionPanel);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("preprocessing")) {
				importAlgorithms(node, AlgorithmBox.getInstance().getPreprocessingAlgorithms(), preprocessingPanel);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("featureSelection")) {
				importAlgorithms(node, AlgorithmBox.getInstance().getFeatureSelectionAlgorithms(), featureSelectionPanel);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("classification")) {
				importAlgorithms(node, AlgorithmBox.getInstance().getClassificationAlgorithms(), classificationPanel);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("evaluation")) {
				importAlgorithms(node, AlgorithmBox.getInstance().getEvaluationAlgorithms(), evaluationPanel);
			}
		}
	}

	/**
	 * Import the setting for the feature extraction tab.
	 * 
	 * @param parent
	 * @param panel
	 */
	private static void importSource(Node parent, FeatureExtractionPanel panel) {
		Node node = null;
		String value = null;

		String featureExtraction = null;

		NodeList nodes = parent.getChildNodes();

		featureExtraction = parent.getAttributes().getNamedItem("featureExtraction").getTextContent().trim();
		if (Boolean.parseBoolean(featureExtraction)) {
			panel.setFeatureExtraction();
		}
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("featureFile")) {
				value = node.getTextContent().trim();
				panel.setFilename(value);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("inputLinesForOneFeature")) {
				value = node.getTextContent().trim();
				if (value.trim().equals("null")) {
					panel.setLinesForFeature(null);
				} else {
					panel.setLinesForFeature(Integer.parseInt(value));
				}
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("inputFileType")) {
				value = node.getTextContent().trim();
				if (value.equals(INPUT_FILE_TYPE.CSV.toString())) {
					panel.setInputFileType(INPUT_FILE_TYPE.CSV);
				} else {
					panel.setInputFileType(INPUT_FILE_TYPE.TAB);
				}
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("outputFile")) {
				value = node.getTextContent().trim();
				panel.setFeatureOutputFile(value);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("inputFiles")) {
				importInputFiles(node, panel);
			}
		}
	}

	/**
	 * Imports the source file list for feature extraction.
	 * 
	 * @param parent
	 * @param panel
	 */
	private static void importInputFiles(Node parent, FeatureExtractionPanel panel) {
		Node node = null;
		String value = null;
		String className = null;
		String subjectID = null;
		InputFile inputFile = null;
		List<InputFile> inputFileList = null;

		inputFileList = new LinkedList<InputFile>();
		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			node = parent.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("file")) {
				className = node.getAttributes().getNamedItem("class").getTextContent().trim();
				subjectID = node.getAttributes().getNamedItem("subjectID").getTextContent().trim();
				value = node.getTextContent().trim();
				inputFile = new InputFile(value);
				inputFile.setClassLabel(className);
				if (!subjectID.equals("null")) {
					inputFile.setSubjectID(Integer.parseInt(subjectID));
				}
				inputFileList.add(inputFile);
			}
		}
		panel.setInputFiles(inputFileList);
	}

	/**
	 * Imports the algorithms for the feature extraction steps.
	 * 
	 * @param parent
	 * @param panel
	 * @throws Exception
	 */
	private static void importFeatureExtractionAlgorithms(Node parent, FeatureExtractionPanel panel) throws Exception {
		Node node = null;
		String className = null;
		Algorithm algorithm = null;
		NodeList nodes = parent.getChildNodes();
		List<Algorithm> list = new LinkedList<Algorithm>();

		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("algorithm")) {
				className = node.getAttributes().getNamedItem("class").getTextContent().trim();
				for (AlgorithmDefinition definition : AlgorithmBox.getInstance().getFeatureExtractionAlgorithms()) {
					if (definition.getClassName().equals(className)) {
						algorithm = definition.createInstance();
						importAlgorithm(node, algorithm);
						list.add(algorithm);
						break;
					}
				}
			}
		}
		panel.setSelectedAlgorithms(list.toArray());
	}

	/**
	 * Imports the algorithms that are below the given XML node.
	 * 
	 * @param parent
	 * @param definitions
	 * @param panel
	 * @throws Exception
	 */
	private static void importAlgorithms(Node parent, List<AlgorithmDefinition> definitions, PipelineStepPanel panel) throws Exception {
		Node node = null;
		String selected = null;
		String className = null;
		Algorithm algorithm = null;
		List<Algorithm> algorithmList = null;
		NodeList nodes = parent.getChildNodes();
		List<List<Algorithm>> allAlgorithms = null;
		Map<String, Boolean> selectedAlgorithmsMap = new HashMap<String, Boolean>();
		Map<String, List<Algorithm>> allAlgorithmsMap = new HashMap<String, List<Algorithm>>();

		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("algorithm")) {
				className = node.getAttributes().getNamedItem("class").getTextContent().trim();
				selected = node.getAttributes().getNamedItem("selected").getTextContent().trim();

				selectedAlgorithmsMap.put(className, Boolean.parseBoolean(selected));
				if (className.equals("null")) {
					allAlgorithmsMap.put(className, null);
				} else {
					for (AlgorithmDefinition definition : definitions) {
						if (definition.getClassName().equals(className)) {
							algorithm = definition.createInstance();
							importAlgorithm(node, algorithm);
							if (allAlgorithmsMap.containsKey(className)) {
								allAlgorithmsMap.get(className).add(algorithm);
							} else {
								algorithmList = new LinkedList<Algorithm>();
								algorithmList.add(algorithm);
								allAlgorithmsMap.put(className, algorithmList);
							}
							break;
						}
					}
				}
			}
		}
		// search algorithms that were not listed in the xml file
		for (AlgorithmDefinition definition : definitions) {
			if (!allAlgorithmsMap.containsKey(definition.getClassName())) {
				algorithmList = new LinkedList<Algorithm>();
				algorithmList.add(definition.createInstance());
				allAlgorithmsMap.put(definition.getClassName(), algorithmList);
				selectedAlgorithmsMap.put(definition.getClassName(), false);
			}
		}
		allAlgorithms = algorithmMapToList(allAlgorithmsMap);
		panel.setAlgorithms(allAlgorithms);
		panel.setSelectedAlgorithms(selectedMapToList(selectedAlgorithmsMap, allAlgorithms));
	}

	/**
	 * Imports the algorithm that is saved in the given XML node.
	 * 
	 * @param parent
	 * @param algorithm
	 */
	private static void importAlgorithm(Node parent, Algorithm algorithm) {
		Node node = null;
		String name = null;
		String value = null;
		Parameter parameter = null;
		NodeList nodes = parent.getChildNodes();
		Parameter gridSearchParameter = null;

		// read current settings
		algorithm.readEditorSettings();
		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("parameter")) {
				name = node.getAttributes().getNamedItem("name").getTextContent().trim();
				value = node.getTextContent().trim();
				if (algorithm instanceof ClassificationAlgorithm && ((ClassificationAlgorithm) algorithm).getEnableGridSearchParameter() != null) {
					gridSearchParameter = ((ClassificationAlgorithm) algorithm).getEnableGridSearchParameter();
					if (name.equals(gridSearchParameter.getName())) {
						parameter = null;
						((ClassificationAlgorithm) algorithm).getEnableGridSearchParameter().setValue(Boolean.parseBoolean(value));
					} else {
						parameter = getParameter(
								ParameterUtilities.mergeParameters(algorithm.getParameters(),
										((ClassificationAlgorithm) algorithm).getGridSearchManagerParameter()), name);
					}
				} else {
					parameter = getParameter(algorithm.getParameters(), name);
				}
				if (parameter != null) {
					ParameterUtilities.assignParameterValue(parameter, value);
				}
			}
		}
		algorithm.getEditor().writeGUISettings();
	}

	/**
	 * Searches a given parameter in the given list.
	 * 
	 * @param parameters
	 * @param name
	 * @return
	 */
	private static Parameter getParameter(Parameter[] parameters, String name) {
		for (Parameter parameter : parameters) {
			if (parameter.getName().equals(name)) {
				return parameter;
			}
		}
		return null;
	}

	/**
	 * Converts a Map to a List.
	 * 
	 * @param selectedAlgorithmsMap
	 * @param allAlgorithms
	 * @return
	 */
	private static List<Boolean> selectedMapToList(Map<String, Boolean> selectedAlgorithmsMap, List<List<Algorithm>> allAlgorithms) {
		List<Algorithm> list = null;
		List<Boolean> selectedAlgorithms = new LinkedList<Boolean>();

		for (int i = 0; i < allAlgorithms.size(); i++) {
			list = allAlgorithms.get(i);
			if (list == null) {
				selectedAlgorithms.add(selectedAlgorithmsMap.get("null"));
			} else {
				if (selectedAlgorithmsMap.get(list.get(0).getDefinition().getClassName())) {
					selectedAlgorithms.add(true);
				} else {
					selectedAlgorithms.add(false);
				}
			}

		}

		return selectedAlgorithms;
	}

	/**
	 * Converts a Map to a List.
	 * 
	 * @param allAlgorithmsMap
	 * @return
	 */
	private static List<List<Algorithm>> algorithmMapToList(Map<String, List<Algorithm>> allAlgorithmsMap) {
		List<List<Algorithm>> allAlgorithms = new LinkedList<List<Algorithm>>();

		for (List<Algorithm> current : allAlgorithmsMap.values()) {
			if (current != null) {
				allAlgorithms.add(current);
			}
		}
		Collections.sort(allAlgorithms, new Comparator<List<Algorithm>>() {
			@Override
			public int compare(List<Algorithm> o1, List<Algorithm> o2) {
				return o1.get(0).getDefinition().getName().compareTo(o2.get(0).getDefinition().getName());
			}

		});
		if (allAlgorithmsMap.containsKey("null")) {
			allAlgorithms.add(null);
		}

		return allAlgorithms;
	}

}
