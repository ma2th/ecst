package ecst.view.featureextraction;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ecst.algorithm.Algorithm;
import ecst.featureextraction.FeatureExtractionModel;
import ecst.featureextraction.FeatureExtractionModel.INPUT_FILE_TYPE;
import ecst.featureextraction.FeatureExtractor;
import ecst.featureextraction.InputFile;
import ecst.utilities.FileUtilities;
import ecst.view.ECST;

/**
 * The JPanels shows the settings for feature extraction.
 * 
 * @author Matthias Ring
 * 
 */
public class GenerateFeaturesPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ECST ecst;
	private DetailsDialog dialog;
	private JButton detailsButton;
	private JTable inputFilesTable;
	private JButton addFilesButton;
	private JButton removeFilesButton;
	private JButton saveFeaturesButton;
	private JButton addFeatureButton;
	private JButton removeFeatureButton;
	private JList featureList;
	private DefaultListModel featureListModel;
	private JTextField featureOutputFileTextField;
	private InputFilesTableModel inputFilesTableModel;
	private JLabel columnsLabel;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @throws Exception
	 */
	public GenerateFeaturesPanel(ECST ecst) throws Exception {
		this.ecst = ecst;
		this.dialog = new DetailsDialog(ecst);

		setupPanel();
	}

	/**
	 * Saves the last used path in the JFileChoosers.
	 * 
	 * @param newPath
	 */
	private void updateLastUsedPath(String newPath) {
		if ((new File(newPath)).isDirectory()) {
			newPath = (new File(newPath)).getParentFile().getAbsolutePath();
		}
		ecst.getProperties().setProperty("ecst.view.featureextraction.GenerateFeaturesPanel.lastUsedPath", newPath);
	}

	/**
	 * Returns the selected feature extraction algorithms.
	 * 
	 * @return
	 */
	public Object[] getSelectedAlgorithms() {
		Object[] algorithms = new Object[featureList.getModel().getSize()];

		for (int i = 0; i < algorithms.length; i++) {
			algorithms[i] = featureList.getModel().getElementAt(i);
		}
		return algorithms;
	}

	/**
	 * Sets the selected feature extraction algorithms.
	 * 
	 * @param algorithms
	 */
	public void setSelectedAlgorithms(Object[] algorithms) {
		featureListModel.removeAllElements();
		for (Object algorithm : algorithms) {
			featureListModel.addElement((Algorithm) algorithm);
		}
	}

	/**
	 * Returns the number of lines for one feature computation.
	 * 
	 * @return
	 */
	public Integer getLinesForFeature() {
		return dialog.getLinesForFeature();
	}

	/**
	 * Sets the number of lines for one feature computation.
	 * 
	 * @param lines
	 */
	public void setLinesForFeature(Integer lines) {
		dialog.setLinesForFeature(lines);
	}

	/**
	 * Returns the input file type.
	 * 
	 * @return
	 */
	public INPUT_FILE_TYPE getInputFileType() {
		return dialog.getInputFileType();
	}

	/**
	 * Sets the input file type.
	 * 
	 * @param type
	 */
	public void setInputFileType(INPUT_FILE_TYPE type) {
		dialog.setInputFileType(type);
	}

	/**
	 * Returns the file name for the output file.
	 * 
	 * @return
	 */
	public String getFeatureOutputFile() {
		return featureOutputFileTextField.getText();
	}

	/**
	 * Sets the file name for the output file.
	 * 
	 * @param file
	 */
	public void setFeatureOutputFile(String file) {
		featureOutputFileTextField.setText(file);
	}

	/**
	 * Returns the list of input files.
	 * 
	 * @return
	 */
	public List<InputFile> getFeatureInputFiles() {
		return inputFilesTableModel.getInputFiles();
	}

	/**
	 * Sets the input file list.
	 * 
	 * @param inputFileList
	 */
	public void setInputFiles(List<InputFile> inputFileList) {
		boolean check = true;
		Integer numberOfColumns = null;

		try {
			inputFilesTableModel.setInputFiles(inputFileList);

			for (InputFile inputFile : inputFileList) {
				if (numberOfColumns == null) {
					numberOfColumns = FeatureExtractor.getNumberOfInputColumns(inputFile, FeatureExtractionModel.convertInputType(getInputFileType()));
				} else if (numberOfColumns != FeatureExtractor.getNumberOfInputColumns(inputFile, FeatureExtractionModel.convertInputType(getInputFileType()))) {
					check = false;
				}
			}
			if (!check) {
				JOptionPane.showMessageDialog(this, "Number of columns is different in input files!", ECST.PROGRAMM_NAME, JOptionPane.WARNING_MESSAGE);
			} else {
				columnsLabel.setText(numberOfColumns + " columns");
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "Error while loading input files!", ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}

	/**
	 * Shows a JFileChooser to add new input files.
	 * 
	 * @param event
	 */
	public void addFilesButtonActionPerformed(ActionEvent event) {
		int i = 0;
		File[] files = null;
		InputFile inputFile = null;
		JFileChooser fileChooser = null;
		List<InputFile> inputFiles = new LinkedList<InputFile>();

		fileChooser = new JFileChooser(ecst.getProperties().getProperty("ecst.view.featureextraction.GenerateFeaturesPanel.lastUsedPath"));
		fileChooser.setMultiSelectionEnabled(true);
		if (fileChooser.showOpenDialog(ecst) == JFileChooser.APPROVE_OPTION) {
			inputFiles.addAll(getFeatureInputFiles());
			files = fileChooser.getSelectedFiles();
			for (File file : files) {
				inputFile = new InputFile(file.getAbsolutePath());
				inputFiles.add(inputFile);
				inputFile.setSubjectID(i++);
				updateLastUsedPath(file.getAbsolutePath());
			}			
			setInputFiles(inputFiles);
		}
	}

	/**
	 * Removes the selected input file.
	 * 
	 * @param event
	 */
	public void removeButtonActionPerformed(ActionEvent event) {
		int firstSelection;
		int selectedCount = inputFilesTable.getSelectedRowCount();

		if (selectedCount > 0) {
			firstSelection = inputFilesTable.getSelectedRow();
			for (int i = 0; i < selectedCount; i++) {
				inputFilesTableModel.removeInputFile(firstSelection);
			}
		}
	}

	/**
	 * Shows a dialog to add new feature extraction algorithms.
	 * 
	 * @param event
	 */
	public void addFeatureButtonActionPerformed(ActionEvent event) {
		FeatureSelectionDialog dialog = null;

		try {
			dialog = new FeatureSelectionDialog(ecst);
			dialog.setVisible(true);
			if (dialog.getAlgorithm() != null) {
				featureListModel.addElement(dialog.getAlgorithm());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(ecst, "Error while creating feature extraction algorithms!", ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	/**
	 * Removes the selected feature.
	 * 
	 * @param event
	 */
	public void removeFeatureButtonActionPerformed(ActionEvent event) {
		if (featureList.getSelectedIndex() >= 0) {
			((DefaultListModel) featureList.getModel()).remove(featureList.getSelectedIndex());
		}
	}

	/**
	 * Shows the DetailsDialog.
	 * 
	 * @param event
	 */
	public void detailsButtonActionPerformed(ActionEvent event) {
		dialog.setVisible(true);
	}

	/**
	 * Shows a JFileChooser to select the output file.
	 * 
	 * @param event
	 */
	public void saveFeaturesButtonActionPerformed(ActionEvent event) {
		File selectedFile = null;

		if ((selectedFile = FileUtilities.showSaveFileChooserARFF(this,
				ecst.getProperties().getProperty("ecst.view.featureextraction.GenerateFeaturesPanel.lastUsedPath"))) != null) {
			featureOutputFileTextField.setText(selectedFile.getAbsolutePath());
			updateLastUsedPath(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Shows the popup to setup the class labels and subject IDs of the input
	 * files.
	 * 
	 * @param event
	 */
	public void maybeShowPopup(MouseEvent event) {
		JPopupMenu popup = null;
		JMenuItem item = null;
		JMenu submenu = null;

		if (event.isPopupTrigger()) {
			popup = new JPopupMenu();

			for (String classLabel : inputFilesTableModel.getClassLabels()) {
				final String copy = classLabel;
				item = new JMenuItem("Change class label to '" + classLabel + "'");
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						inputFilesTableModel.setClassLabels(inputFilesTable.getSelectedRows(), copy);
					}
				});
				popup.add(item);
			}
			popup.addSeparator();
			submenu = new JMenu("Change subject ID to");
			for (Integer subjectID : inputFilesTableModel.getSubjectIDs()) {
				final String copy = String.valueOf(subjectID);
				item = new JMenuItem(copy);
				item.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent event) {
						inputFilesTableModel.setSubjectIDs(inputFilesTable.getSelectedRows(), Integer.parseInt(copy));
					}
				});
				submenu.add(item);
			}
			popup.add(submenu);
			popup.show(event.getComponent(), event.getX(), event.getY());
		}
	}

	/**
	 * Creates this panel.
	 * 
	 * @throws Exception
	 */
	private void setupPanel() throws Exception {
		setLayout(new GridBagLayout());
		setupInputFilesPanel();
		setupFeaturePanel();
		setupSavePanel();

		inputFilesTableModel = new InputFilesTableModel();
		inputFilesTable.setModel(inputFilesTableModel);
	}

	/**
	 * Creates this panel.
	 */
	private void setupSavePanel() {
		JPanel panel = null;
		GridBagConstraints c = null;

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		featureOutputFileTextField = new JTextField();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 2);
		panel.add(featureOutputFileTextField, c);

		saveFeaturesButton = new JButton("Choose...");
		saveFeaturesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				saveFeaturesButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		panel.add(saveFeaturesButton, c);

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Output file", TitledBorder.LEFT, TitledBorder.TOP));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(panel, c);
	}

	/**
	 * Creates this panel.
	 */
	private void setupInputFilesPanel() {
		JPanel panel = null;
		JPanel tmpPanel = null;
		GridBagConstraints c = null;
		JScrollPane scrollPane = null;

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		inputFilesTable = new JTable();
		inputFilesTable.setAutoCreateRowSorter(true);
		inputFilesTable.setFillsViewportHeight(true);
		inputFilesTable.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent event) {
				maybeShowPopup(event);
			}

			@Override
			public void mousePressed(MouseEvent event) {
				maybeShowPopup(event);
			}

			@Override
			public void mouseClicked(MouseEvent event) {
			}

			@Override
			public void mouseEntered(MouseEvent event) {
			}

			@Override
			public void mouseExited(MouseEvent event) {
			}
		});
		scrollPane = new JScrollPane(inputFilesTable);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 5;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 3, 0);
		panel.add(scrollPane, c);

		addFilesButton = new JButton("\uFF0B");
		addFilesButton.setMargin(new Insets(0, 2, 0, 2));
		addFilesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				addFilesButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		panel.add(addFilesButton, c);

		removeFilesButton = new JButton("\uFF0D");
		removeFilesButton.setMargin(new Insets(0, 2, 0, 2));
		removeFilesButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				removeButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		panel.add(removeFilesButton, c);

		tmpPanel = new JPanel();
		tmpPanel.setPreferredSize(new Dimension(10, 1));
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tmpPanel, c);

		detailsButton = new JButton("Details...");
		detailsButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				detailsButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		panel.add(detailsButton, c);

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Input files", TitledBorder.LEFT, TitledBorder.TOP));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		add(panel, c);
	}

	/**
	 * Creates this panel.
	 * 
	 * @throws Exception
	 */
	private void setupFeaturePanel() throws Exception {
		JPanel panel = null;
		GridBagConstraints c = null;
		JScrollPane scrollPane = null;

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		featureList = new JList();
		featureListModel = new DefaultListModel();
		featureList.setModel(featureListModel);
		scrollPane = new JScrollPane(featureList);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 3;
		c.insets = new Insets(0, 0, 3, 0);
		c.fill = GridBagConstraints.BOTH;
		panel.add(scrollPane, c);

		addFeatureButton = new JButton("\uFF0B");
		addFeatureButton.setMargin(new Insets(0, 2, 0, 2));
		addFeatureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				addFeatureButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		panel.add(addFeatureButton, c);

		removeFeatureButton = new JButton("\uFF0D");
		removeFeatureButton.setMargin(new Insets(0, 2, 0, 2));
		removeFeatureButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				removeFeatureButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		panel.add(removeFeatureButton, c);

		columnsLabel = new JLabel(" # columns");
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_END;
		panel.add(columnsLabel, c);

		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Features", TitledBorder.LEFT, TitledBorder.TOP));
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 5, 0, 0);
		add(panel, c);
	}

}