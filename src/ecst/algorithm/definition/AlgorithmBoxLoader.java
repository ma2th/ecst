package ecst.algorithm.definition;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * This class load the contents of the XML configuration document into the class
 * AlgorithmBox.
 * 
 * @author Matthias Ring
 * 
 */
public class AlgorithmBoxLoader {

	/**
	 * Load all algorithm definitions and operation definitions from the XML
	 * document.
	 */
	public static void parseConfigFile(List<AlgorithmDefinition> featureExtractionAlgorithms,
			List<AlgorithmDefinition> preprocessingAlgorithms, List<AlgorithmDefinition> featureSelectionAlgorithms,
			List<AlgorithmDefinition> classificationAlgorithms, List<AlgorithmDefinition> evaluationAlgorithms,
			List<OperationDefinition> operationDefinitions) throws Exception {
		Node root = null;
		Node node = null;
		Document doc = null;
		NodeList nodes = null;

		doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(AlgorithmBox.XML_FILE));
		doc.getDocumentElement().normalize();
		root = doc.getDocumentElement();
		nodes = root.getChildNodes();

		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeName().equals("operations")) {
				parseOperationDefinitions(node, operationDefinitions);
			}
		}

		for (int i = 0; i < nodes.getLength(); i++) {
			node = nodes.item(i);
			if (node.getNodeName().equals("featureExtraction")) {
				parseAlgorithm(node, featureExtractionAlgorithms, operationDefinitions, true);
			} else if (node.getNodeName().equals("preprocessing")) {
				parseAlgorithm(node, preprocessingAlgorithms, operationDefinitions, true);
			} else if (node.getNodeName().equals("featureSelection")) {
				parseAlgorithm(node, featureSelectionAlgorithms, null, false);
			} else if (node.getNodeName().equals("classification")) {
				parseAlgorithm(node, classificationAlgorithms, operationDefinitions, true);
			} else if (node.getNodeName().equals("evaluation")) {
				parseAlgorithm(node, evaluationAlgorithms, null, false);
			}
		}
	}

	/**
	 * Parse the XML operations definitions.
	 * 
	 * @param parent
	 * @param operationDefinitions
	 * @throws Exception
	 */
	private static void parseOperationDefinitions(Node parent, List<OperationDefinition> operationDefinitions) throws Exception {
		Node child = null;

		for (int j = 0; j < parent.getChildNodes().getLength(); j++) {
			child = parent.getChildNodes().item(j);
			if (child.getNodeType() == Node.ELEMENT_NODE && child.getNodeName().equals("operation")) {
				operationDefinitions.add(new OperationDefinition(child.getAttributes().getNamedItem("name").getTextContent().trim(), child
						.getAttributes().getNamedItem("description").getTextContent().trim(), child
						.getAttributes().getNamedItem("descriptionLatex").getTextContent().trim()));
			}
		}
	}

	/**
	 * Parses the XML algorithm definition.
	 * 
	 * @param parent
	 * @param featureExtractionAlgorithms
	 * @param operationDefinitions
	 * @param workPhaseAlgorithm
	 * @throws Exception
	 */
	private static void parseAlgorithm(Node parent, List<AlgorithmDefinition> featureExtractionAlgorithms,
			List<OperationDefinition> operationDefinitions, boolean workPhaseAlgorithm) throws Exception {
		Node node = null;
		AlgorithmDefinition algorithm = null;

		for (int j = 0; j < parent.getChildNodes().getLength(); j++) {
			node = parent.getChildNodes().item(j);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("algorithm")) {
				algorithm = new AlgorithmDefinition(node.getAttributes().getNamedItem("name").getTextContent().trim(), node.getAttributes()
						.getNamedItem("class").getTextContent().trim());
				if (workPhaseAlgorithm) {
					parseOperationsAndSpace(node, algorithm, operationDefinitions);
				}

				featureExtractionAlgorithms.add(algorithm);
			}
		}
	}

	/**
	 * Parses the XML operations and space definitions.
	 * 
	 * @param parent
	 * @param algorithm
	 * @param operationDefinitions
	 */
	private static void parseOperationsAndSpace(Node parent, AlgorithmDefinition algorithm, List<OperationDefinition> operationDefinitions) {
		Node node = null;

		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			node = parent.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("operations")) {
				parseOperations(node, algorithm, operationDefinitions);
			} else if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("space")) {
				parseSpace(node, algorithm);
			}
		}
	}

	/**
	 * Parses the XML space definitions.
	 * 
	 * @param parent
	 * @param algorithm
	 */
	private static void parseSpace(Node parent, AlgorithmDefinition algorithm) {
		String type = null;
		String name = null;
		Attr attribute = null;
		String dependency = null;
		StaticSpace staticSpace = null;
		List<String> multiplierList = null;

		for (int i = 0; i < parent.getAttributes().getLength(); i++) {
			attribute = (Attr) parent.getAttributes().item(i);
			if (attribute.getName().equals("description")) {
				name = attribute.getValue();
			} else if (attribute.getName().equals("dependency")) {
				dependency = attribute.getValue();
			} else if (attribute.getName().equals("type")) {
				type = attribute.getValue();
			}
		}

		multiplierList = getMultiplierList(parent);
		staticSpace = new StaticSpace(type, name, multiplierList, dependency);

		algorithm.addStaticSpace(staticSpace);
	}

	/**
	 * Parses the XML operation definitions.
	 * @param parent
	 * @param algorithm
	 * @param operationDefinitions
	 */
	private static void parseOperations(Node parent, AlgorithmDefinition algorithm, List<OperationDefinition> operationDefinitions) {
		Attr attribute = null;
		boolean found = false;
		String dependency = null;
		List<String> multiplierList = null;
		StaticOperations operations = null;
		Map<OperationDefinition, Integer> map = new HashMap<OperationDefinition, Integer>();

		for (int i = 0; i < parent.getAttributes().getLength(); i++) {
			attribute = (Attr) parent.getAttributes().item(i);
			if (attribute.getName().equals("dependency")) {
				dependency = attribute.getValue();
			} else {
				found = false;
				for (OperationDefinition definition : operationDefinitions) {
					if (attribute.getName().equals(definition.getName())) {
						map.put(definition, Integer.parseInt(attribute.getValue()));
						found = true;
						break;
					}
				}
				if (!found) {
					throw new IllegalArgumentException("Operation unknown: " + attribute.getName());
				}
			}
		}

		multiplierList = getMultiplierList(parent);
		operations = new StaticOperations(map, dependency, multiplierList);

		algorithm.addStaticOperations(operations);
	}

	/**
	 * Parses the XML multiplier definitions.
	 * @param parent
	 * @return
	 */
	private static List<String> getMultiplierList(Node parent) {
		Node node = null;
		List<String> list = new LinkedList<String>();

		for (int i = 0; i < parent.getChildNodes().getLength(); i++) {
			node = parent.getChildNodes().item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE && node.getNodeName().equals("multiplier")) {
				list.add(((Attr) node.getAttributes().getNamedItem("name")).getValue());
			}
		}

		return list;
	}

}
