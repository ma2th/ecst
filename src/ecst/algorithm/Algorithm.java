package ecst.algorithm;

import ecst.algorithm.definition.AlgorithmDefinition;
import ecst.algorithm.parameter.Parameter;

/**
 * Basis class for all algorithms that contains the definition of the algorithm
 * from the XML configuration document and the graphical user interface to
 * adjust the algorithm parameters.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class Algorithm {

	private Integer instanceCounter;
	private AlgorithmEditor editor;
	private AlgorithmDefinition definition;

	public abstract void initParameters();

	public abstract Parameter[] getParameters();

	public abstract Class<? extends Object> getImplementingClass();

	/**
	 * Constructor.
	 */
	public Algorithm() {
		this.editor = new AlgorithmEditor();
	}

	/**
	 * Initializes all parameters of this algorithm and adds them to the editor.
	 */
	public void init() {
		initParameters();
		editor.addParameters(getParameters(), getImplementingClass());
	}

	/**
	 * Reads the user settings from the editor.
	 */
	public void readEditorSettings() {
		editor.readGUISettings();
	}

	/**
	 * Returns the editor of this algorithm instance.
	 * 
	 * @return
	 */
	public AlgorithmEditor getEditor() {
		return editor;
	}

	/**
	 * Returns the definition of this algorithm instance.
	 * 
	 * @return
	 */
	public AlgorithmDefinition getDefinition() {
		return definition;
	}

	/**
	 * Sets the definition for this algorithm instance.
	 * 
	 * @param definition
	 */
	public void setDefinition(AlgorithmDefinition definition) {
		this.definition = definition;
	}

	/**
	 * Returns the number of instances of this algorithm that were created by
	 * the user.
	 * 
	 * @return
	 */
	public Integer getInstanceCounter() {
		return instanceCounter;
	}

	/**
	 * Sets the number of instances of this algorithm that were created by the
	 * user.
	 * 
	 * @param instanceCounter
	 */
	public void setInstanceCounter(Integer instanceCounter) {
		this.instanceCounter = instanceCounter;
	}

	/**
	 * Returns a string representation of this algorithm instance.
	 */
	public String toString() {
		return "Algorithm instance of definition: " + definition;
	}

}
