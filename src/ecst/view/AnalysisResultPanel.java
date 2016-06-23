package ecst.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import ecst.algorithm.Algorithm;
import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureExtractionAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.combiner.Combiner;
import ecst.combiner.CombinerInputModel;
import ecst.combiner.CombinerRunnable;
import ecst.combiner.ProgressDialog;
import ecst.featureextraction.FeatureExtractionModel;
import ecst.io.AnalysisExport;
import ecst.io.ClassificationSystemExport;
import ecst.io.ConfigurationExport;
import ecst.io.ConfigurationImport;
import ecst.utilities.CommonUtilities;
import ecst.utilities.FileUtilities;
import ecst.view.result.FilterDialog;
import ecst.view.result.ResultList;

/**
 * This JPanel presents the analysis result.
 * 
 * @author Matthias Ring
 * 
 */
public class AnalysisResultPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private ECST ecst;
	private ResultList listPanel;
	private JMenuItem startMenuItem;
	private JMenuItem filterMenuItem;
	private JMenuItem saveXMLMenuItem;
	private JMenuItem saveCSVMenuItem;
	private JMenuItem saveLatexMenuItem;
	private JMenuItem saveConfigurationMenuItem;
	private JMenuItem loadConfigurationMenuItem;
	private JMenuItem exportModelMenuItem;
	private PipelineStepPanel preprocessingPanel;
	private PipelineStepPanel featureSelectionPanel;
	private PipelineStepPanel classificationPanel;
	private PipelineStepPanel evaluationPanel;
	private FeatureExtractionPanel featureExtractionPanel;
	private String lastUsedPath;
	private File exportFile;
	private boolean quitAfterCombiner;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @param featureExtractionPanel
	 * @param preprocessingPanel
	 * @param featureSelectionPanel
	 * @param classificationPanel
	 * @param evaluationPanel
	 */
	public AnalysisResultPanel(ECST ecst, FeatureExtractionPanel featureExtractionPanel,
			PipelineStepPanel preprocessingPanel, PipelineStepPanel featureSelectionPanel,
			PipelineStepPanel classificationPanel, PipelineStepPanel evaluationPanel) {
		this.quitAfterCombiner = false;
		
		this.ecst = ecst;
		this.evaluationPanel = evaluationPanel;
		this.featureExtractionPanel = featureExtractionPanel;
		this.preprocessingPanel = preprocessingPanel;
		this.classificationPanel = classificationPanel;
		this.featureSelectionPanel = featureSelectionPanel;
		this.lastUsedPath = ecst.getProperties().getProperty("ecst.view.AnalysisResultPanel.lastUsedPath");
		setupPanel();
	}
	
	public void setExportFile(File file) {
		this.exportFile = file;
	}
	
	public void setQuitAfterCombiner(boolean quit) {
		this.quitAfterCombiner = quit;
	}

	/**
	 * Saves the last used path in JFileChoosers.
	 * 
	 * @param newPath
	 */
	private void updateLastUsedPath(String newPath) {
		if ((new File(newPath)).isDirectory()) {
			newPath = (new File(newPath)).getParentFile().getAbsolutePath();
		}

		lastUsedPath = newPath;
		ecst.getProperties().setProperty("ecst.view.AnalysisResultPanel.lastUsedPath", newPath);
	}

	/**
	 * Starts the complexity analysis.
	 * 
	 * @param event
	 */
	public void startButtonActionPerformed(ActionEvent event) {
		Thread thread = null;
		Combiner combiner = null;
		ProgressDialog dialog = null;
		CombinerInputModel model = null;
		CombinerRunnable combinerRunnable = null;

		if (!featureExtractionPanel.isFeatureExtraction()
				&& (featureExtractionPanel.getFilename() == null || featureExtractionPanel.getFilename().equals(""))) {
			JOptionPane.showMessageDialog(this, "Please choose an input file!", ECST.PROGRAMM_NAME,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (featureExtractionPanel.isFeatureExtraction() && (featureExtractionPanel.getSelectedAlgorithms().length == 0
				|| featureExtractionPanel.getFeatureInputFiles().size() == 0)) {
			JOptionPane.showMessageDialog(this, "Please choose input files and features!", ECST.PROGRAMM_NAME,
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (preprocessingPanel.getSelectedAlgorithms().size() == 0
				|| featureSelectionPanel.getSelectedAlgorithms().size() == 0
				|| classificationPanel.getSelectedAlgorithms().size() == 0
				|| evaluationPanel.getSelectedAlgorithms().size() == 0) {
			JOptionPane.showMessageDialog(this, "Please select at least one algorithm in each pipeline step!",
					ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
			return;
		}

		model = createCombinerModel();
		combiner = new Combiner(model);
		combinerRunnable = new CombinerRunnable(combiner, listPanel, exportFile, quitAfterCombiner, ecst);
		thread = new Thread(combinerRunnable);
		dialog = new ProgressDialog(ecst);

		combiner.addProgressListener(dialog);
		combinerRunnable.setDialog(dialog);
		dialog.setCombiner(combiner);

		thread.start();
		dialog.setVisible(true);

		filterMenuItem.setEnabled(true);
		saveXMLMenuItem.setEnabled(true);
		saveLatexMenuItem.setEnabled(true);
		saveCSVMenuItem.setEnabled(true);
		exportModelMenuItem.setEnabled(true);
	}

	/**
	 * Shows the filter dialog for the complexity analysis.
	 * 
	 * @param event
	 */
	public void filterButtonActionPerformed(ActionEvent event) {
		FilterDialog dialog = new FilterDialog(ecst, listPanel.getCurrentFilter());

		dialog.setVisible(true);
		if (dialog.getFilterModel() != null) {
			listPanel.setFilter(dialog.getFilterModel());
		}
	}

	/**
	 * Shows a JFileChooser to save the configuration.
	 * 
	 * @param event
	 */
	public void saveConfigurationButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showSaveFileChooserXML(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			updateLastUsedPath(file.getAbsolutePath());
			try {
				ConfigurationExport.exportXML(file, featureExtractionPanel.getFilename(),
						featureExtractionPanel.isFeatureExtraction(), createFeatureExtractionModel(),
						CommonUtilities.algorithmArrayToList(featureExtractionPanel.getSelectedAlgorithms()),
						preprocessingPanel.getAlgorithms(), featureSelectionPanel.getAlgorithms(),
						classificationPanel.getAlgorithms(), evaluationPanel.getAlgorithms(),
						preprocessingPanel.getSelectedAlgorithms(), featureSelectionPanel.getSelectedAlgorithms(),
						classificationPanel.getSelectedAlgorithms(), evaluationPanel.getSelectedAlgorithms());
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ecst, "File could not be saved!", ECST.PROGRAMM_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Shows a JFileChooser to load the configuration.
	 * 
	 * @param event
	 */
	public void loadConfigurationButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showOpenFileChooserXML(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			loadConfigurationFile(file);
		}
	}
	
	public void loadConfigurationFile(File file) {
		try {
			updateLastUsedPath(file.getAbsolutePath());
			ConfigurationImport.importXML(file, featureExtractionPanel, preprocessingPanel, featureSelectionPanel, classificationPanel, evaluationPanel);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(ecst, "File could not be loaded!", ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Shows a JFileChooser to save the result as XML.
	 * 
	 * @param event
	 */
	public void saveResultAsXMLButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showSaveFileChooserXML(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			updateLastUsedPath(file.getAbsolutePath());
			try {
				AnalysisExport.exportXML(listPanel.getModel(), file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ecst, "File could not be saved!", ECST.PROGRAMM_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Shows a JFileChooser to save the result as CSV.
	 * 
	 * @param event
	 */
	public void saveResultAsCSVButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showSaveFileChooserCSV(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			updateLastUsedPath(file.getAbsolutePath());
			try {
				AnalysisExport.exportCSV(listPanel.getModel(), file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ecst, "File could not be saved!", ECST.PROGRAMM_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Shows a JFileChooser to save the result as Latex.
	 * 
	 * @param event
	 */
	public void saveResultAsLatexButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showSaveFileChooserLatex(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			updateLastUsedPath(file.getAbsolutePath());
			try {
				AnalysisExport.exportLatex(listPanel.getModel(), file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ecst, "File could not be saved!", ECST.PROGRAMM_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Shows a JFileChooser to export the trained classification system.
	 * 
	 * @param event
	 */
	public void exportModelButtonActionPerformed(ActionEvent event) {
		File file = FileUtilities.showSaveFileChooserXML(ecst.getRootPane(), lastUsedPath);
		if (file != null) {
			updateLastUsedPath(file.getAbsolutePath());
			try {
				ClassificationSystemExport.exportXML(listPanel.getModel(), file);
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(ecst, "File could not be saved!", ECST.PROGRAMM_NAME,
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	/**
	 * Internal method that combines the user settings into one object.
	 * 
	 * @return
	 */
	private FeatureExtractionModel createFeatureExtractionModel() {
		return new FeatureExtractionModel(featureExtractionPanel.getLinesForFeature(),
				featureExtractionPanel.getFeatureOutputFile(), featureExtractionPanel.getFeatureInputFiles(),
				featureExtractionPanel.getInputFileType());
	}

	/**
	 * Internal method that combines the user settings into one object.
	 * 
	 * @return
	 */
	private CombinerInputModel createCombinerModel() {
		CombinerInputModel model = new CombinerInputModel();
		FeatureExtractionModel featureExtractionModel = null;

		if (featureExtractionPanel.isFeatureExtraction()) {
			featureExtractionModel = createFeatureExtractionModel();
			model.setFeatureExtractionModel(featureExtractionModel);
			model.setExtractFeatures(true);

			for (int i = 0; i < featureExtractionPanel.getSelectedAlgorithms().length; i++) {
				model.addFeatureExtractionAlgorithm(
						(FeatureExtractionAlgorithm) featureExtractionPanel.getSelectedAlgorithms()[i]);
			}
		} else {
			model.setInputFile(featureExtractionPanel.getFilename());
			model.setExtractFeatures(false);
		}

		for (Algorithm algorithm : preprocessingPanel.getSelectedAlgorithms()) {
			if (algorithm == null) {
				model.addPreprocessingAlgorithm(null);
			} else {
				model.addPreprocessingAlgorithm((PreprocessingAlgorithm) algorithm);
			}
		}
		for (Algorithm algorithm : featureSelectionPanel.getSelectedAlgorithms()) {
			if (algorithm == null) {
				model.addFeatureSelectionAlgorithm(null);
			} else {
				model.addFeatureSelectionAlgorithm((FeatureSelectionAlgorithm) algorithm);
			}
		}
		for (Algorithm algorithm : classificationPanel.getSelectedAlgorithms()) {
			model.addClassificationAlgorithm((ClassificationAlgorithm) algorithm);
		}
		for (Algorithm algorithm : evaluationPanel.getSelectedAlgorithms()) {
			model.addEvaluationAlgorithm((EvaluationAlgorithm) algorithm);
		}

		return model;
	}

	/**
	 * Creates the GUI.
	 */
	private void setupPanel() {
		JButton button = null;
		JPanel tmpPanel = null;
		GridBagConstraints c = null;
		final JPopupMenu startButtonPopup = new JPopupMenu();
		final JPopupMenu pipelineButtonPopup = new JPopupMenu();
		final JPopupMenu classificationSystemButtonPopup = new JPopupMenu();

		setLayout(new GridBagLayout());

		listPanel = new ResultList(ecst);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 0, 7, 0);
		add(listPanel, c);

		tmpPanel = new JPanel();
		tmpPanel.setPreferredSize(new Dimension(100, 1));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		add(tmpPanel, c);

		exportModelMenuItem = new JMenuItem(new AbstractAction("Export parameters as XML...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				exportModelButtonActionPerformed(event);
			}
		});
		button = new JButton("Classification system \u25BE");
		button.setMargin(new Insets(3, 1, 3, 1));
		exportModelMenuItem.setEnabled(false);
		classificationSystemButtonPopup.add(exportModelMenuItem);
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				classificationSystemButtonPopup.show(e.getComponent(), 0, e.getComponent().getHeight());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 10);
		add(button, c);

		loadConfigurationMenuItem = new JMenuItem(new AbstractAction("Load configuration...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				loadConfigurationButtonActionPerformed(event);
			}
		});
		saveConfigurationMenuItem = new JMenuItem(new AbstractAction("Save configuration...") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				saveConfigurationButtonActionPerformed(event);
			}
		});

		button = new JButton("Pipeline configuration \u25BE");
		button.setMargin(new Insets(3, 1, 3, 1));
		pipelineButtonPopup.add(loadConfigurationMenuItem);
		pipelineButtonPopup.add(saveConfigurationMenuItem);
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				pipelineButtonPopup.show(e.getComponent(), 0, e.getComponent().getHeight());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 0, 10);
		add(button, c);

		startMenuItem = new JMenuItem(new AbstractAction("Start analysis") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				startButtonActionPerformed(event);
			}
		});
		filterMenuItem = new JMenuItem(new AbstractAction("Filter analysis...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				filterButtonActionPerformed(event);
			}
		});
		saveXMLMenuItem = new JMenuItem(new AbstractAction("Export analysis as XML...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				saveResultAsXMLButtonActionPerformed(event);
			}
		});
		saveCSVMenuItem = new JMenuItem(new AbstractAction("Export analysis as CSV...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				saveResultAsCSVButtonActionPerformed(event);
			}
		});
		saveLatexMenuItem = new JMenuItem(new AbstractAction("Export analysis as Latex...") {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent event) {
				saveResultAsLatexButtonActionPerformed(event);
			}
		});

		filterMenuItem.setEnabled(false);
		saveXMLMenuItem.setEnabled(false);
		saveCSVMenuItem.setEnabled(false);
		saveLatexMenuItem.setEnabled(false);

		button = new JButton("Complexity analysis \u25BE");
		button.setMargin(new Insets(3, 1, 3, 1));
		startButtonPopup.add(startMenuItem);
		startButtonPopup.addSeparator();
		startButtonPopup.add(filterMenuItem);
		startButtonPopup.addSeparator();
		startButtonPopup.add(saveXMLMenuItem);
		startButtonPopup.add(saveCSVMenuItem);
		startButtonPopup.add(saveLatexMenuItem);
		button.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				startButtonPopup.show(e.getComponent(), 0, e.getComponent().getHeight());
			}
		});
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 1;
		add(button, c);

		setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
	}

}
