package ecst.algorithm.definition;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import ecst.view.ECST;

/**
 * This class represents the content of the XML configuration document.
 * 
 * @author Matthias Ring
 * 
 */
public class AlgorithmBox {

	private static AlgorithmBox singleton;

	public static final String XML_FILE = "config/Algorithms.xml";

	private List<AlgorithmDefinition> featureExtractionAlgorithms;
	private List<AlgorithmDefinition> preprocessingAlgorithms;
	private List<AlgorithmDefinition> featureSelectionAlgorithms;
	private List<AlgorithmDefinition> classificationAlgorithms;
	private List<AlgorithmDefinition> evaluationAlgorithms;
	private List<OperationDefinition> operationDefinitions;

	/**
	 * Only one instance of this class is necessary.
	 * @return
	 */
	public static AlgorithmBox getInstance() {
		if (singleton == null) {
			singleton = new AlgorithmBox();
		}
		return singleton;
	}

	/**
	 * Constructor. Loads the XML configuration document.
	 */
	private AlgorithmBox() {
		try {
			featureExtractionAlgorithms = new LinkedList<AlgorithmDefinition>();
			preprocessingAlgorithms = new LinkedList<AlgorithmDefinition>();
			featureSelectionAlgorithms = new LinkedList<AlgorithmDefinition>();
			classificationAlgorithms = new LinkedList<AlgorithmDefinition>();
			evaluationAlgorithms = new LinkedList<AlgorithmDefinition>();
			operationDefinitions = new LinkedList<OperationDefinition>();

			AlgorithmBoxLoader.parseConfigFile(featureExtractionAlgorithms, preprocessingAlgorithms, featureSelectionAlgorithms,
					classificationAlgorithms, evaluationAlgorithms, operationDefinitions);

			Collections.sort(featureExtractionAlgorithms);
			Collections.sort(preprocessingAlgorithms);
			Collections.sort(featureSelectionAlgorithms);
			Collections.sort(classificationAlgorithms);
			Collections.sort(evaluationAlgorithms);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Error while parsing configuration file '" + XML_FILE + "'\n" + e.getMessage(),
					ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Returns the feature extration algorithms defined in the configuration document.
	 * @return
	 */
	public List<AlgorithmDefinition> getFeatureExtractionAlgorithms() {
		return featureExtractionAlgorithms;
	}

	/**
	 * Returns the preprocessing algorithms defined in the configuration document.
	 * @return
	 */
	public List<AlgorithmDefinition> getPreprocessingAlgorithms() {
		return preprocessingAlgorithms;
	}

	/**
	 * Returns the feature selection algorithms defined in the configuration document.
	 * @return
	 */
	public List<AlgorithmDefinition> getFeatureSelectionAlgorithms() {
		return featureSelectionAlgorithms;
	}

	/**
	 * Returns the classification algorithms defined in the configuration document.
	 * @return
	 */
	public List<AlgorithmDefinition> getClassificationAlgorithms() {
		return classificationAlgorithms;
	}

	/**
	 * Returns the evaluation algorithms defined in the configuration document.
	 * @return
	 */
	public List<AlgorithmDefinition> getEvaluationAlgorithms() {
		return evaluationAlgorithms;
	}

	
	/**
	 * Returns the operations defined in the configuration document.
	 * @return
	 */
	public List<OperationDefinition> getOperationDefinitions() {
		return operationDefinitions;
	}

}
