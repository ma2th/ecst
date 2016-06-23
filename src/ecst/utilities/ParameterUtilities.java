package ecst.utilities;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.text.NumberFormatter;

import ecst.algorithm.Algorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.algorithm.parameter.SelectedParameterRenderer;
import weka.core.Option;
import weka.core.OptionHandler;
import weka.core.Utils;

/**
 * This class contains various method for usage with the Parameter class.
 * 
 * @author Matthias Ring
 * 
 */
public class ParameterUtilities {

	/**
	 * Converts the parameters into an option array in WEKA format.
	 * 
	 * @param algorithm
	 * @return
	 * @throws Exception
	 */
	public static String[] buildOptionsString(Algorithm algorithm) throws Exception {
		return Utils.splitOptions(buildOptionsString(algorithm.getParameters()));
	}

	/**
	 * Converts the parameters into an option array in WEKA format.
	 * 
	 * @param parameters
	 * @return
	 */
	public static String buildOptionsString(Parameter[] parameters) {
		boolean linkedToItem = false;
		String linkedToItemString = "";
		String optionsString = "";
		String tmp = "";
		int position;
		SelectedParameterItem item = null;
		SelectedParameter selectedParameter = null;

		if (parameters == null) {
			return "";
		}

		for (Parameter parameter : parameters) {
			linkedToItem = false;
			linkedToItemString = "";

			if (parameter.getValue() == null || (parameter.getType() != Parameter.TYPE.SELECTED_PARAMETER && parameter.getOptionString() == null)
					|| parameter.getLinkedToSelectedParameterItem() != null) {
				continue;
			}

			if (Parameter.TYPE.BOOLEAN.equals(parameter.getType()) || Parameter.TYPE.INTEGER.equals(parameter.getType())
					|| Parameter.TYPE.DOUBLE.equals(parameter.getType()) || Parameter.TYPE.LONG.equals(parameter.getType())
					|| Parameter.TYPE.STRING.equals(parameter.getType())) {
				optionsString += ParameterUtilities.buildOptionsString(parameter);
			} else if (Parameter.TYPE.SELECTED_PARAMETER.equals(parameter.getType())) {
				selectedParameter = (SelectedParameter) parameter.getValue();
				item = selectedParameter.getItems().get(selectedParameter.getSelectedIndex());

				if (item.getOptionString() != null) {
					for (Parameter linkedToThisItemParameter : parameters) {
						if (linkedToThisItemParameter.getLinkedToSelectedParameterItem() != null
								&& linkedToThisItemParameter.getLinkedToSelectedParameterItem().equals(item)) {
							linkedToItem = true;
							linkedToItemString += ParameterUtilities.buildOptionsString(linkedToThisItemParameter);
						}
					}

					if (!linkedToItem) {
						optionsString += " " + item.getOptionString() + " ";
					} else {
						tmp = item.getOptionString() + linkedToItemString;
						// insert quotes just in the case that there are spaces in
						// the option's argument
						position = tmp.indexOf(" ");
						tmp = tmp.substring(0, position) + " \"" + tmp.substring(position + 1).trim() + "\"";
						optionsString += " " + tmp + " ";
					}
				}
			} else {
				throw new IllegalArgumentException("Unknown parameter type!");
			}
		}
		return optionsString.trim();
	}

	public static String buildOptionsString(Parameter parameter) {
		String optionStringAddOn = "";

		if (Parameter.TYPE.BOOLEAN.equals(parameter.getType())) {
			if ((Boolean) parameter.getValue()) {
				optionStringAddOn = " " + parameter.getOptionString() + " ";
			}
		} else if (Parameter.TYPE.INTEGER.equals(parameter.getType())) {
			optionStringAddOn = " " + parameter.getOptionString() + " " + (Integer) parameter.getValue() + " ";
		} else if (Parameter.TYPE.DOUBLE.equals(parameter.getType())) {
			optionStringAddOn = " " + parameter.getOptionString() + " " + (Double) parameter.getValue() + " ";
		} else if (Parameter.TYPE.LONG.equals(parameter.getType())) {
			optionStringAddOn = " " + parameter.getOptionString() + " " + (Long) parameter.getValue() + " ";
		} else if (Parameter.TYPE.STRING.equals(parameter.getType())) {
			optionStringAddOn = " " + parameter.getOptionString() + " \"" + (String) parameter.getValue() + "\" ";
		}

		return optionStringAddOn;
	}

	/**
	 * Merges the given arrays into one array.
	 * 
	 * @param parametersArray
	 * @return
	 */
	public static Parameter[] mergeParameters(Parameter[]... parametersArray) {
		Parameter[] result = null;
		List<Parameter> list = new LinkedList<Parameter>();

		for (Parameter[] parameters : parametersArray) {
			if (parameters != null) {
				for (Parameter parameter : parameters) {
					list.add(parameter);
				}
			}
		}
		result = new Parameter[list.size()];
		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}
		return result;
	}

	/**
	 * Creates a new SelectedParameter instances with the given items for
	 * selection.
	 * 
	 * @param name
	 * @param items
	 * @return
	 */
	public static Parameter createSelectedParameter(String name, SelectedParameterItem... items) {
		Parameter parameter = null;
		List<SelectedParameterItem> list = new LinkedList<SelectedParameterItem>();

		for (SelectedParameterItem item : items) {
			list.add(item);
		}
		parameter = new Parameter(new SelectedParameter(list, 0), name, Parameter.TYPE.SELECTED_PARAMETER, null);
		return parameter;
	}

	/**
	 * Assigns the given value to the given parameter.
	 * 
	 * @param parameter
	 * @param value
	 */
	public static void assignParameterValue(Parameter parameter, String value) {
		SelectedParameter item = null;

		if (value.equals("null")) {
			parameter.setValue(null);
		} else {
			if (parameter.getType().equals(Parameter.TYPE.INTEGER)) {
				parameter.setValue(Integer.parseInt(value));
			} else if (parameter.getType().equals(Parameter.TYPE.LONG)) {
				parameter.setValue(Long.parseLong(value));
			} else if (Parameter.TYPE.DOUBLE.equals(parameter.getType())) {
				parameter.setValue(Double.parseDouble(value));
			} else if (parameter.getType().equals(Parameter.TYPE.BOOLEAN)) {
				parameter.setValue(Boolean.parseBoolean(value));
			} else if (parameter.getType().equals(Parameter.TYPE.STRING)) {
				parameter.setValue(value);
			} else if (Parameter.TYPE.SELECTED_PARAMETER.equals(parameter.getType())) {
				item = (SelectedParameter) parameter.getValue();
				item.setSelectedIndex(Integer.parseInt(value));
			} else if (parameter.getType().equals(Parameter.TYPE.FILE_NAME)) {
				parameter.setValue(value);
			}
		}
	}

	/**
	 * Returns the editor component from the panel.
	 * 
	 * @param panel
	 * @return
	 */
	public static JComponent getEditorFromPanel(JComponent panel) {
		JComponent recursiveComponent = null;

		if (panel instanceof JPanel) {
			for (Component comp : panel.getComponents()) {
				if (comp instanceof JPanel) {
					recursiveComponent = getEditorFromPanel((JComponent) comp);
					if (recursiveComponent != null) {
						return recursiveComponent;
					}
				}
				if ("editor".equals(comp.getName())) {
					return (JComponent) comp;
				}
			}
			return null;
		} else {
			return panel;
		}
	}

	/**
	 * Sets the value of the parameter to the GUI element.
	 * 
	 * @param parameter
	 * @param component
	 */
	public static void updateEditorValue(Parameter parameter, JComponent component) {
		component = getEditorFromPanel(component);

		if (parameter.getType().equals(Parameter.TYPE.INTEGER) || parameter.getType().equals(Parameter.TYPE.LONG)
				|| Parameter.TYPE.DOUBLE.equals(parameter.getType())) {
			((JFormattedTextField) component).setValue(parameter.getValue());
		} else if (parameter.getType().equals(Parameter.TYPE.BOOLEAN)) {
			((JComboBox) component).setSelectedItem(parameter.getValue());
		} else if (parameter.getType().equals(Parameter.TYPE.STRING)) {
			((JTextField) component).setText((String) parameter.getValue());
		} else if (Parameter.TYPE.SELECTED_PARAMETER.equals(parameter.getType())) {
			((JComboBox) component).setSelectedIndex(((SelectedParameter) parameter.getValue()).getSelectedIndex());
		} else if (parameter.getType().equals(Parameter.TYPE.FILE_NAME)) {
			((JTextField) component).setText((String) parameter.getValue());
		}
	}

	/**
	 * Reads the GUI value and puts it into the parameter.
	 */
	public static void assignParameterValue(Parameter parameter, JComponent component) {
		SelectedParameter item = null;

		component = getEditorFromPanel(component);

		if (parameter.getType().equals(Parameter.TYPE.INTEGER)) {
			if (getFormattedTextFieldValue(component) == null) {
				parameter.setValue(null);
			} else {
				parameter.setValue(new Integer(((Number) getFormattedTextFieldValue(component)).intValue()));
			}
		} else if (parameter.getType().equals(Parameter.TYPE.LONG)) {
			if (getFormattedTextFieldValue(component) == null) {
				parameter.setValue(null);
			} else {
				parameter.setValue(new Long(((Number) getFormattedTextFieldValue(component)).longValue()));
			}
		} else if (Parameter.TYPE.DOUBLE.equals(parameter.getType())) {
			if (getFormattedTextFieldValue(component) == null) {
				parameter.setValue(null);
			} else {
				parameter.setValue(new Double(((Number) getFormattedTextFieldValue(component)).doubleValue()));
			}
		} else if (parameter.getType().equals(Parameter.TYPE.BOOLEAN)) {
			parameter.setValue(((JComboBox) component).getSelectedItem());
		} else if (parameter.getType().equals(Parameter.TYPE.STRING)) {
			if (getTextFieldString(component) == null || getTextFieldString(component).equals("")) {
				parameter.setValue(null);
			} else {
				parameter.setValue(getTextFieldString(component));
			}
		} else if (Parameter.TYPE.SELECTED_PARAMETER.equals(parameter.getType())) {
			item = (SelectedParameter) parameter.getValue();
			item.setSelectedIndex(((JComboBox) component).getSelectedIndex());
		} else if (parameter.getType().equals(Parameter.TYPE.FILE_NAME)) {
			if (getTextFieldString(component) == null || getTextFieldString(component).equals("")) {
				parameter.setValue(null);
			} else {
				parameter.setValue(getTextFieldString(component));
			}
		}
	}

	/**
	 * Returns the value of a formatted text field.
	 * 
	 * @param component
	 * @return
	 */
	private static Object getFormattedTextFieldValue(JComponent component) {
		return ((JFormattedTextField) component).getValue();
	}

	/**
	 * Returns the value of a text field.
	 * 
	 * @param component
	 * @return
	 */
	private static String getTextFieldString(JComponent component) {
		return ((JTextField) component).getText();
	}

	/**
	 * Creates a panel with an info field about the options.
	 * 
	 * @param parameter
	 * @param parent
	 * @param implementingClass
	 * @return
	 */
	public static JComponent createEditorPanel(Parameter parameter, JComponent parent, Class<? extends Object> implementingClass) {
		JComponent component = createEditor(parameter, parent);

		if (OptionHandler.class.isAssignableFrom(implementingClass)) {
			return setupInfoPanel(component, parameter, implementingClass);
		} else {
			return component;
		}
	}

	/**
	 * Creates the corresponding GUI element for the given parameter.
	 * 
	 * @param parameter
	 * @param parent
	 * @return
	 */
	private static JComponent createEditor(Parameter parameter, JComponent parent) {
		if (Parameter.TYPE.INTEGER.equals(parameter.getType())) {
			return createIntegerEditor(parameter);
		} else if (Parameter.TYPE.LONG.equals(parameter.getType())) {
			return createLongEditor(parameter);
		} else if (Parameter.TYPE.DOUBLE.equals(parameter.getType())) {
			return createDoubleEditor(parameter);
		} else if (Parameter.TYPE.BOOLEAN.equals(parameter.getType())) {
			return createBooleanEditor(parameter);
		} else if (Parameter.TYPE.STRING.equals(parameter.getType())) {
			return createStringEditor(parameter);
		} else if (Parameter.TYPE.SELECTED_PARAMETER.equals(parameter.getType())) {
			return createSelectedParameterEditor(parameter);
		} else if (Parameter.TYPE.FILE_NAME.equals(parameter.getType())) {
			return createFilenameEditor(parameter, parent);
		} else {
			throw new IllegalArgumentException("Unknown parameter type");
		}
	}

	/**
	 * Returns the character describing the option.
	 * 
	 * @param optionString
	 * @return
	 */
	private static String getOptionCharacter(String optionString) {
		optionString = optionString.trim();
		if (optionString.startsWith("-")) {
			optionString = optionString.substring(1);
		}
		if (optionString.startsWith("-")) {
			optionString = optionString.substring(1);
		}
		if (optionString.indexOf(" ") > -1) {
			optionString = optionString.substring(0, optionString.indexOf(" "));
		}
		return optionString;
	}

	/**
	 * Creates an info field for all options.
	 * 
	 * @param component
	 * @param parameter
	 * @param implementingClass
	 * @return
	 */
	private static JComponent setupInfoPanel(JComponent component, Parameter parameter, Class<? extends Object> implementingClass) {
		@SuppressWarnings("rawtypes")
		Enumeration en = null;
		Option option = null;
		String optionString = null;
		OptionHandler optionHandler = null;

		if (parameter.getType().equals(Parameter.TYPE.SELECTED_PARAMETER)) {
			optionString = getOptionCharacter(((SelectedParameter) parameter.getValue()).getItems().get(0).getOptionString());
		} else if (parameter.getOptionString() != null) {
			optionString = getOptionCharacter(parameter.getOptionString());
		} else {
			return component;
		}

		try {
			optionHandler = (OptionHandler) implementingClass.newInstance();
			en = optionHandler.listOptions();
			while (en != null && en.hasMoreElements()) {
				option = (Option) en.nextElement();
				if (option.name().equals(optionString)) {
					return createInfoPanel(component, option);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return component;
	}

	/**
	 * Creates an info field for the given options.
	 * 
	 * @param component
	 * @param option
	 * @return
	 */
	private static JPanel createInfoPanel(JComponent component, Option option) {
		BorderLayout layout = new BorderLayout();
		final JPanel panel = new JPanel();
		final JTextPane textPane = new JTextPane();
		final BasicArrowButton arrowButton = new BasicArrowButton(SwingConstants.SOUTH);

		panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

		layout.setHgap(2);
		layout.setVgap(2);
		panel.setLayout(layout);
		panel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

		component.setName("editor");
		panel.add(component, BorderLayout.CENTER);

		textPane.setText(option.description().replaceAll("\t", " ").trim() + "\nSynopsis: " + option.synopsis());
		textPane.setEditable(false);
		textPane.setFont(textPane.getFont().deriveFont(11f));
		textPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		textPane.setVisible(false);
		panel.add(textPane, BorderLayout.SOUTH);

		arrowButton.setEnabled(true);
		arrowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				if (textPane.isVisible()) {
					textPane.setVisible(false);
					panel.revalidate();
					arrowButton.setDirection(SwingConstants.SOUTH);
				} else {
					arrowButton.setDirection(SwingConstants.NORTH);
					textPane.setVisible(true);
				}
			}
		});
		panel.add(arrowButton, BorderLayout.EAST);

		return panel;
	}

	/**
	 * Creates a NumberFormatter object for the usage in JFormattedTextFields.
	 * 
	 * @param format
	 * @return
	 */
	public static NumberFormatter createNumberFormatter(NumberFormat format) {
		return new NumberFormatter(format) {

			private static final long serialVersionUID = 1L;

			public String valueToString(Object value) throws ParseException {
				if (value == null) {
					return "";
				} else {
					return super.valueToString(value);
				}
			}

			public Object stringToValue(String text) throws ParseException {
				if ("".equals(text)) {
					return null;
				}
				return super.stringToValue(text);
			}
		};
	}

	/**
	 * Creates an GUI element for integer parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createIntegerEditor(Parameter parameter) {
		NumberFormat integerFormat = null;
		NumberFormatter integerFormatter = null;
		JFormattedTextField formattedTextField = null;

		integerFormat = NumberFormat.getInstance();
		integerFormat.setParseIntegerOnly(true);
		integerFormatter = createNumberFormatter(integerFormat);
		integerFormatter.setValueClass(Integer.class);
		formattedTextField = new JFormattedTextField(integerFormatter);
		formattedTextField.setValue(parameter.getValue());

		return formattedTextField;
	}

	/**
	 * Creates an GUI element for long parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createLongEditor(Parameter parameter) {
		NumberFormat integerFormat = null;
		NumberFormatter integerFormatter = null;
		JFormattedTextField formattedTextField = null;

		integerFormat = NumberFormat.getInstance();
		integerFormat.setParseIntegerOnly(true);
		integerFormatter = createNumberFormatter(integerFormat);
		integerFormatter.setValueClass(Long.class);
		formattedTextField = new JFormattedTextField(integerFormatter);
		formattedTextField.setValue(parameter.getValue());

		return formattedTextField;
	}

	/**
	 * Creates an GUI element for double parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createDoubleEditor(Parameter parameter) {
		NumberFormatter doubleFormatter = null;
		JFormattedTextField formattedTextField = null;

		doubleFormatter = createNumberFormatter(NumberFormat.getInstance());
		doubleFormatter.setValueClass(Double.class);
		formattedTextField = new JFormattedTextField(doubleFormatter);
		formattedTextField.setValue(parameter.getValue());

		return formattedTextField;
	}

	/**
	 * Creates an GUI element for boolean parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createBooleanEditor(Parameter parameter) {
		JComboBox comboBox = null;

		comboBox = new JComboBox(new Boolean[] { true, false });
		comboBox.setSelectedItem(parameter.getValue());
		comboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
				if ((Boolean) value) {
					return super.getListCellRendererComponent(list, "yes", index, isSelected, cellHasFocus);
				} else {
					return super.getListCellRendererComponent(list, "no", index, isSelected, cellHasFocus);
				}
			}
		});

		return comboBox;
	}

	/**
	 * Creates an GUI element for string parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createStringEditor(Parameter parameter) {
		JTextField textField = null;

		textField = new JTextField();
		textField.setText((String) parameter.getValue());

		return textField;
	}

	/**
	 * Creates an GUI element for SelecteParameter parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createSelectedParameterEditor(Parameter parameter) {
		JComboBox comboBox = null;
		SelectedParameter selectedParameter = null;

		selectedParameter = (SelectedParameter) parameter.getValue();
		comboBox = new JComboBox(selectedParameter.getItems().toArray());
		comboBox.setSelectedIndex(selectedParameter.getSelectedIndex());
		comboBox.setRenderer(new SelectedParameterRenderer());

		return comboBox;
	}

	/**
	 * Creates an GUI element for filename parameters.
	 * 
	 * @param parameter
	 * @return
	 */
	public static JComponent createFilenameEditor(Parameter parameter, final JComponent parent) {
		JPanel panel = null;
		JButton button = null;
		final JTextField textFieldFileName = new JTextField();

		textFieldFileName.setText((String) parameter.getValue());
		textFieldFileName.setName("editor");
		button = new JButton("Choose...");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				File selectedFile = null;

				if ((selectedFile = FileUtilities.showOpenFileChooserARFF(parent, textFieldFileName.getText())) != null) {
					textFieldFileName.setText(selectedFile.getAbsolutePath());
				}
			}

		});
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(textFieldFileName, BorderLayout.CENTER);
		panel.add(button, BorderLayout.EAST);

		return panel;
	}

}
