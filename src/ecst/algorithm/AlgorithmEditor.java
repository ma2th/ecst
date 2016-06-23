package ecst.algorithm;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ecst.algorithm.parameter.Parameter;
import ecst.utilities.ParameterUtilities;

/**
 * This is the graphical user interface for the user to adjust the algorithm
 * parameters. The interface is dynamically created according to the given
 * parameters.
 * 
 * @author Matthias Ring
 * 
 */
public class AlgorithmEditor extends JPanel {

	private static final long serialVersionUID = 1L;

	private int row;
	private Parameter enableGridSearch;
	private JCheckBox gridSearchCheckBox;
	private Map<Parameter, JComponent> parameterComponentMap;
	private Map<Parameter, JComponent> gridSearchParameterMap;

	/**
	 * Constructor.
	 */
	public AlgorithmEditor() {
		row = 0;
		parameterComponentMap = new HashMap<Parameter, JComponent>();
		gridSearchParameterMap = new HashMap<Parameter, JComponent>();

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	}

	/**
	 * Returns if the user wants to perform a grid search.
	 * 
	 * @return
	 */
	public Parameter getEnableGridSearch() {
		return enableGridSearch;
	}

	/**
	 * Sets the value of the grid search JCheckbox.
	 * 
	 * @param enableGridSearch
	 */
	public void setEnableGridSearch(Parameter enableGridSearch) {
		this.enableGridSearch = enableGridSearch;
	}

	/**
	 * Adds the parameters that should be displayed in the GUI and the
	 * implementing class that may provide a description string of the
	 * algorithm.
	 * 
	 * @param parameters
	 * @param implementingClass
	 */
	public void addParameters(Parameter[] parameters, Class<? extends Object> implementingClass) {
		createParameterEditors(parameters, parameterComponentMap, implementingClass);
		setupPanel();
	}

	/**
	 * Adds the parameters for grid search that should be displayed in the GUI
	 * and the implementing class that may provide a description string of the
	 * algorithm.
	 * 
	 * @param parameters
	 * @param implementingClass
	 */
	public void addGridSearchParameters(Parameter[] parameters, Class<? extends Object> implementingClass) {
		createParameterEditors(parameters, gridSearchParameterMap, implementingClass);
		setupPanel();
	}

	/**
	 * Internal method to manage the parameters.
	 * 
	 * @param parameters
	 * @param map
	 * @param implementingClass
	 */
	private void createParameterEditors(Parameter[] parameters, Map<Parameter, JComponent> map, Class<? extends Object> implementingClass) {
		if (parameters == null) {
			return;
		}
		for (Parameter parameter : parameters) {
			map.put(parameter, ParameterUtilities.createEditorPanel(parameter, this, implementingClass));
		}
	}

	/**
	 * Reads the user settings from the GUI and saves them in the Parameter
	 * objects.
	 */
	public void readGUISettings() {
		readSettings(parameterComponentMap);

		if (enableGridSearch != null) {
			readSettings(gridSearchParameterMap);
			enableGridSearch.setValue(gridSearchCheckBox.isSelected());
		}
	}

	/**
	 * Internal method to read the user settings.
	 */
	private void readSettings(Map<Parameter, JComponent> map) {
		for (Parameter parameter : map.keySet()) {
			ParameterUtilities.assignParameterValue(parameter, map.get(parameter));
		}
	}

	/**
	 * Sets the values of the Parameter objects to the GUI elements.
	 */
	public void writeGUISettings() {
		writeSettings(parameterComponentMap);

		if (enableGridSearch != null) {
			writeSettings(gridSearchParameterMap);
			enableGridSearchArea((Boolean) enableGridSearch.getValue());
			gridSearchCheckBox.setSelected((Boolean) enableGridSearch.getValue());
		}
	}

	/**
	 * Internal method to update the GUI elements.
	 * 
	 * @param map
	 */
	private void writeSettings(Map<Parameter, JComponent> map) {
		for (Parameter parameter : map.keySet()) {
			ParameterUtilities.updateEditorValue(parameter, map.get(parameter));
		}
	}

	/**
	 * Creates and initalizes the GUI.
	 */
	private void setupPanel() {
		JPanel panel = null;
		GridBagConstraints c = null;

		row = 0;
		removeAll();

		addParameters(parameterComponentMap, true);

		if (enableGridSearch != null) {
			panel = new JPanel();
			gridSearchCheckBox = new JCheckBox("Enable grid search for parameter");
			gridSearchCheckBox.setSelected((Boolean) enableGridSearch.getValue());
			gridSearchCheckBox.setOpaque(true);
			gridSearchCheckBox.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent event) {
					gridSearchCheckBoxActionPerformed(event);
				}

			});
			panel.setLayout(new BorderLayout());
			panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.BLACK));
			panel.add(gridSearchCheckBox, BorderLayout.CENTER);

			c = new GridBagConstraints();
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(20, 0, 0, 0);
			c.gridx = 0;
			c.gridy = row;
			c.weightx = 1.0;
			c.gridwidth = 2;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(panel, c);

			row++;

			addParameters(gridSearchParameterMap, false);
		}

		panel = new JPanel();
		panel.setSize(new Dimension(10, 10));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = row;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.BOTH;
		add(panel, c);

		row++;
	}

	/**
	 * Enables or disables the parameters for grid search settings.
	 * 
	 * @param enable
	 */
	private void enableGridSearchArea(boolean enable) {
		for (JComponent component : gridSearchParameterMap.values()) {
			ParameterUtilities.getEditorFromPanel(component).setEnabled(enable);
		}
		enableGridSearch.setValue(enable);
	}

	/**
	 * Returns if the users selected grid search.
	 * 
	 * @param event
	 */
	public void gridSearchCheckBoxActionPerformed(ActionEvent event) {
		enableGridSearchArea(gridSearchCheckBox.isSelected());
	}

	/**
	 * Internal method that adds the GUI elements of the parametes to this GUI.
	 * 
	 * @param map
	 * @param enable
	 */
	private void addParameters(Map<Parameter, JComponent> map, boolean enable) {
		JLabel label = null;
		JComponent component = null;
		GridBagConstraints c = null;
		TreeSet<Parameter> treeSet = new TreeSet<Parameter>();

		treeSet.addAll(map.keySet());
		for (Parameter parameter : treeSet) {
			label = new JLabel("<html>" + parameter.getName() + ": </html>");
			label.setMinimumSize(new Dimension(220, 45));
			label.setPreferredSize(new Dimension(220, 45));
			c = new GridBagConstraints();
			c.gridx = 0;
			c.gridy = row;
			c.anchor = GridBagConstraints.LINE_START;
			c.insets = new Insets(0, 0, 0, 10);
			add(label, c);

			component = map.get(parameter);
			ParameterUtilities.getEditorFromPanel(component).setEnabled(enable);
			c = new GridBagConstraints();
			c.gridx = 1;
			c.gridy = row;
			c.weightx = 1.0;
			c.fill = GridBagConstraints.HORIZONTAL;
			add(component, c);

			row++;
		}
		revalidate();
	}

}
