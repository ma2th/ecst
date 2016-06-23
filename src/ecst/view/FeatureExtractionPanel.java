package ecst.view;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import ecst.featureextraction.FeatureExtractionModel.INPUT_FILE_TYPE;
import ecst.featureextraction.InputFile;
import ecst.view.featureextraction.GenerateFeaturesPanel;
import ecst.view.featureextraction.LoadFeaturePanel;

/**
 * This JPanel presents the GUI elements for feature extraction.
 * 
 * @author Matthias Ring
 * 
 */
public class FeatureExtractionPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ECST ecst;
	private JRadioButton loadRadioButton;
	private JRadioButton generateRadioButton;
	private LoadFeaturePanel loadFeaturePanel;
	private GenerateFeaturesPanel generateFeaturePanel;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @throws Exception
	 */
	public FeatureExtractionPanel(ECST ecst) throws Exception {
		this.ecst = ecst;
		setupPanel();
		enableComponents(generateFeaturePanel, false);
		enableComponents(loadFeaturePanel, true);
	}

	/**
	 * Returns the filename if features are loaded from file.
	 * 
	 * @return
	 */
	public String getFilename() {
		return loadFeaturePanel.getFilename();
	}

	/**
	 * Sets the filename if features are loaded from file.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		loadFeaturePanel.setFilename(filename);
	}

	/**
	 * Returns if feature extraction is selected.
	 * 
	 * @return
	 */
	public boolean isFeatureExtraction() {
		return generateRadioButton.isSelected();
	}

	/**
	 * Enables the GUI elements for feature extraction.
	 */
	public void setFeatureExtraction() {
		generateRadioButton.setSelected(true);
		enableComponents(generateFeaturePanel, true);
		enableComponents(loadFeaturePanel, false);
	}

	/**
	 * Returns the number of lines for one feature computation.
	 * 
	 * @return
	 */
	public Integer getLinesForFeature() {
		return generateFeaturePanel.getLinesForFeature();
	}

	/**
	 * Sets the number of lines for one feature computation.
	 * 
	 * @param lines
	 */
	public void setLinesForFeature(Integer lines) {
		generateFeaturePanel.setLinesForFeature(lines);
	}

	/**
	 * Returns the input file type.
	 * 
	 * @return
	 */
	public INPUT_FILE_TYPE getInputFileType() {
		return generateFeaturePanel.getInputFileType();
	}

	/**
	 * Sets the input file type.
	 * 
	 * @param type
	 */
	public void setInputFileType(INPUT_FILE_TYPE type) {
		generateFeaturePanel.setInputFileType(type);
	}

	/**
	 * Returns the output file name.
	 * 
	 * @return
	 */
	public String getFeatureOutputFile() {
		return generateFeaturePanel.getFeatureOutputFile();
	}

	/**
	 * Sets the output file name.
	 * 
	 * @param file
	 */
	public void setFeatureOutputFile(String file) {
		generateFeaturePanel.setFeatureOutputFile(file);
	}

	/**
	 * Returns the list of input files for feature extraction.
	 * 
	 * @return
	 */
	public List<InputFile> getFeatureInputFiles() {
		return generateFeaturePanel.getFeatureInputFiles();
	}

	/**
	 * Sets the list of input files for feature extraction.
	 * 
	 * @param inputFileList
	 */
	public void setInputFiles(List<InputFile> inputFileList) {
		generateFeaturePanel.setInputFiles(inputFileList);
	}

	/**
	 * Returns the selected algorithms for feature extraction.
	 * 
	 * @return
	 */
	public Object[] getSelectedAlgorithms() {
		return generateFeaturePanel.getSelectedAlgorithms();
	}

	/**
	 * Sets the selected algorithms for feature extraction.
	 * 
	 * @param algorithms
	 */
	public void setSelectedAlgorithms(Object[] algorithms) {
		generateFeaturePanel.setSelectedAlgorithms(algorithms);
	}

	/**
	 * Enables or disables the component in the given panel.
	 * 
	 * @param panel
	 * @param enable
	 */
	private void enableComponents(JPanel panel, boolean enable) {
		for (Component comp : panel.getComponents()) {
			if (comp instanceof JPanel) {
				enableComponents((JPanel) comp, enable);
			} else {
				comp.setEnabled(enable);
			}
		}
	}

	/**
	 * Enables or disables the comonents of feature extraction/feature loading
	 * 
	 * @param event
	 */
	public void loadRadioButtonActionPerformed(ActionEvent event) {
		enableComponents(generateFeaturePanel, false);
		enableComponents(loadFeaturePanel, true);
	}

	/**
	 * Enables or disables the comonents of feature extraction/feature loading
	 * 
	 * @param event
	 */
	public void generateRadioButtonActionPerformed(ActionEvent event) {
		setFeatureExtraction();
	}

	/**
	 * Creates the GUI elements.
	 * 
	 * @throws Exception
	 */
	private void setupPanel() throws Exception {
		ButtonGroup group = null;
		GridBagConstraints c = null;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

		loadRadioButton = new JRadioButton("No feature extraction (load features from file)");
		loadRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				loadRadioButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(loadRadioButton, c);

		loadFeaturePanel = new LoadFeaturePanel(ecst);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(5, 25, 0, 0);
		add(loadFeaturePanel, c);

		generateRadioButton = new JRadioButton("Use feature extraction");
		generateRadioButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				generateRadioButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(10, 0, 0, 0);
		add(generateRadioButton, c);

		generateFeaturePanel = new GenerateFeaturesPanel(ecst);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(5, 25, 0, 0);
		add(generateFeaturePanel, c);

		group = new ButtonGroup();
		group.add(loadRadioButton);
		group.add(generateRadioButton);
		loadRadioButton.setSelected(true);
	}

}
