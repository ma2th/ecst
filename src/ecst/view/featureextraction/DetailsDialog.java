package ecst.view.featureextraction;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.text.NumberFormatter;

import ecst.featureextraction.FeatureExtractionModel.INPUT_FILE_TYPE;
import ecst.utilities.ParameterUtilities;
import ecst.view.ECST;

/**
 * A JDialog to set up the details of the feature extraction.
 * 
 * @author Matthias Ring
 * 
 */
public class DetailsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private final String CSV = "Comma separated values (.csv)";
	private final String TAB = "Tabulator separated values (.tab)";

	private JButton okButton;
	private JComboBox inputFileTypeComboBox;
	private JFormattedTextField linesForFeatureTextField;
	private JRadioButton oneFeaturePerFile;
	private JRadioButton multipleFeaturesPerLine;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 */
	public DetailsDialog(ECST ecst) {
		setupDialog(ecst);
	}

	/**
	 * Returns the number of lines for one feature computation.
	 */
	public Integer getLinesForFeature() {
		if (oneFeaturePerFile.isSelected()) {
			return null;
		} else {
			return (Integer) linesForFeatureTextField.getValue();
		}
	}

	/**
	 * Sets the number of lines for one feature computation.
	 * 
	 * @param lines
	 */
	public void setLinesForFeature(Integer lines) {
		if (lines == null) {
			oneFeaturePerFile.setSelected(true);
		} else {
			linesForFeatureTextField.setValue(lines);
			multipleFeaturesPerLine.setSelected(true);
		}
	}

	/**
	 * Returns the type of the input files.
	 * 
	 * @return
	 */
	public INPUT_FILE_TYPE getInputFileType() {
		if (inputFileTypeComboBox.getSelectedItem().equals(CSV)) {
			return INPUT_FILE_TYPE.CSV;
		} else {
			return INPUT_FILE_TYPE.TAB;
		}
	}

	/**
	 * Sets the type of the input files.
	 * 
	 * @param type
	 */
	public void setInputFileType(INPUT_FILE_TYPE type) {
		if (type.equals(INPUT_FILE_TYPE.CSV)) {
			inputFileTypeComboBox.setSelectedItem(CSV);
		} else {
			inputFileTypeComboBox.setSelectedItem(TAB);
		}
	}

	/**
	 * Closes this dialog.
	 * 
	 * @param event
	 */
	public void okButtonActionPerformed(ActionEvent event) {
		dispose();
	}

	/**
	 * Creates the GUI elements.
	 * 
	 * @param ecst
	 */
	private void setupDialog(ECST ecst) {
		JLabel label = null;
		JPanel panel = new JPanel();
		ButtonGroup group = new ButtonGroup();
		GridBagConstraints c = null;
		NumberFormat integerFormat = null;
		NumberFormatter integerFormatter = null;

		panel.setLayout(new GridBagLayout());

		label = new JLabel("Column separator:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 5);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		inputFileTypeComboBox = new JComboBox();
		inputFileTypeComboBox.addItem(TAB);
		inputFileTypeComboBox.addItem(CSV);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 2;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 10, 0);
		panel.add(inputFileTypeComboBox, c);

		oneFeaturePerFile = new JRadioButton("Extract one feature per file");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(oneFeaturePerFile, c);

		multipleFeaturesPerLine = new JRadioButton("Extract one feature every ");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		panel.add(multipleFeaturesPerLine, c);

		integerFormat = NumberFormat.getInstance();
		integerFormat.setParseIntegerOnly(true);
		integerFormatter = ParameterUtilities.createNumberFormatter(integerFormat);
		integerFormatter.setValueClass(Integer.class);
		linesForFeatureTextField = new JFormattedTextField(integerFormatter);
		linesForFeatureTextField.setValue(10);
		linesForFeatureTextField.setPreferredSize(new Dimension(50, 20));
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		panel.add(linesForFeatureTextField, c);

		label = new JLabel(" lines");
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		multipleFeaturesPerLine.setSelected(true);
		group.add(oneFeaturePerFile);
		group.add(multipleFeaturesPerLine);

		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				okButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.gridwidth = 3;
		c.insets = new Insets(5, 0, 0, 0);
		panel.add(okButton, c);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		setTitle("Details");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ecst);
	}

}
