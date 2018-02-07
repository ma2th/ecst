package ecst.view;

import java.awt.Cursor;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

import ecst.algorithm.definition.AlgorithmBox;
import ecst.io.ConfigurationImport;
import ecst.utilities.FileUtilities;

/**
 * This is the main class that shows the Embedded Classification Software
 * Toolbox.
 * 
 * @author Matthias Ring
 * 
 */
public class ECST extends JFrame {

	private static final long serialVersionUID = 1L;

	public static final String PROGRAMM_NAME = "Embedded Classifcation Software Toolbox";
	public static final String VERSION = "1.7.2";
	public static final String URL = "http://www.tinyurl.com/ecst-project";

	private static final String CONFIGURATION_OPTION_SHORT = "c";
	private static final String ARFF_FILE_OPTION_SHORT = "f";
	private static final String EXPORT_OPTION_SHORT = "e";
	private static final String QUIT_OPTION_SHORT = "q";
	private static final String HELP_OPTION_SHORT = "h";
	private static final String CONFIGURATION_OPTION_LONG = "configuration";
	private static final String ARFF_FILE_OPTION_LONG = "file";
	private static final String EXPORT_OPTION_LONG = "export";
	private static final String QUIT_OPTION_LONG = "quit";
	private static final String HELP_OPTION_LONG = "help";

	private PipelineStepPanel preprocessingPanel;
	private PipelineStepPanel featureSelectionPanel;
	private PipelineStepPanel classificationPanel;
	private PipelineStepPanel evaluationPanel;
	private FeatureExtractionPanel inputPanel;
	private AnalysisResultPanel resultPanel;
	private Properties properties;
	private JTabbedPane tabbedPane;

	/**
	 * Construtor.
	 * 
	 * @throws Exception
	 */
	public ECST() throws Exception {
		properties = FileUtilities.loadPropertiesFile(
				System.getProperty("user.home") + System.getProperty("file.separator") + "ecst.properties");

		setupPosition();
		setupFrame();
	}

	/**
	 * Returns the properties object that is saved in the user's home.
	 * 
	 * @return
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Sets the postion of this frame up.
	 */
	private void setupPosition() {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		setTitle(PROGRAMM_NAME);
		setSize(880, 550);
		setLocation((dim.width - getSize().width) / 2, (dim.height - getSize().height) / 2);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	/**
	 * Creates the content of this frame.
	 */
	private void setupFrame() throws Exception {
		JLabel label = null;
		GridBagConstraints c = null;

		setLayout(new GridBagLayout());

		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(10, 5, 5, 5);
		c.fill = GridBagConstraints.BOTH;
		createTabbedPane();
		add(tabbedPane, c);

		JComponent glassPane = (JComponent) getRootPane().getGlassPane();
		glassPane.setLayout(new GridBagLayout());
		glassPane.setVisible(true);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.insets = new Insets(12, 5, 5, 6);
		c.anchor = GridBagConstraints.NORTHEAST;
		label = new JLabel("<html>ECST " + VERSION + "</html>");
		label.setForeground(getBackground().darker());
		label.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent arg0) {
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				setCursor(new Cursor(Cursor.HAND_CURSOR));
			}

			@Override
			public void mouseClicked(MouseEvent arg0) {
				try {
					Desktop.getDesktop().browse(new URI(URL));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		glassPane.add(label, c);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				FileUtilities.savePropertiesFile(properties,
						System.getProperty("user.home") + System.getProperty("file.separator") + "ecst.properties");
			}
		});
	}

	/**
	 * Creates the tab pane.
	 */
	private void createTabbedPane() throws Exception {
		tabbedPane = new JTabbedPane();

		inputPanel = new FeatureExtractionPanel(this);
		preprocessingPanel = new PipelineStepPanel(AlgorithmBox.getInstance().getPreprocessingAlgorithms(), true);
		featureSelectionPanel = new PipelineStepPanel(AlgorithmBox.getInstance().getFeatureSelectionAlgorithms(), true);
		classificationPanel = new PipelineStepPanel(AlgorithmBox.getInstance().getClassificationAlgorithms(), false);
		evaluationPanel = new PipelineStepPanel(AlgorithmBox.getInstance().getEvaluationAlgorithms(), false);
		resultPanel = new AnalysisResultPanel(this, inputPanel, preprocessingPanel, featureSelectionPanel,
				classificationPanel, evaluationPanel);

		tabbedPane.addTab("Feature extraction", inputPanel);
		tabbedPane.addTab("Preprocessing", preprocessingPanel);
		tabbedPane.addTab("Feature selection", featureSelectionPanel);
		tabbedPane.addTab("Classification", classificationPanel);
		tabbedPane.addTab("Evaluation", evaluationPanel);
		tabbedPane.addTab("Complexity analysis", resultPanel);
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}

	private void parseCommandLine(String[] args) throws Exception {
		Options options = null;
		CommandLine cmd = null;
		HelpFormatter formatter = null;
		CommandLineParser parser = null;

		options = new Options();
		options.addOption(CONFIGURATION_OPTION_SHORT, CONFIGURATION_OPTION_LONG, true,
				"Load configuration file (.xml) and start immediately");
		options.addOption(ARFF_FILE_OPTION_SHORT, ARFF_FILE_OPTION_LONG, true,
				"Process this file (.arff) instead of file from configuration");
		options.addOption(EXPORT_OPTION_SHORT, EXPORT_OPTION_LONG, true, "Save analysis result to file (.csv)");
		options.addOption(QUIT_OPTION_SHORT, QUIT_OPTION_LONG, false,
				"Quit program after all combinations have been tested");
		options.addOption(HELP_OPTION_SHORT, HELP_OPTION_LONG, false, "Displays this help");

		parser = new DefaultParser();
		cmd = parser.parse(options, args);

		if (cmd.hasOption(CONFIGURATION_OPTION_LONG) && cmd.getOptionValue(CONFIGURATION_OPTION_LONG) != null) {
			ConfigurationImport.importXML(new File(cmd.getOptionValue(CONFIGURATION_OPTION_LONG)), inputPanel,
					preprocessingPanel, featureSelectionPanel, classificationPanel, evaluationPanel);

			if (cmd.hasOption(ARFF_FILE_OPTION_LONG) && cmd.getOptionValue(ARFF_FILE_OPTION_LONG) != null) {
				inputPanel.setFilename(cmd.getOptionValue(ARFF_FILE_OPTION_LONG));
			}

			tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);

			if (cmd.hasOption(EXPORT_OPTION_LONG) && cmd.getOptionValue(EXPORT_OPTION_LONG) != null) {
				resultPanel.setExportFile(new File(cmd.getOptionValue(EXPORT_OPTION_LONG)));
			}

			if (cmd.hasOption(QUIT_OPTION_LONG)) {
				resultPanel.setQuitAfterCombiner(true);
			}

			resultPanel.startButtonActionPerformed(null);
		}

		if (cmd.hasOption(HELP_OPTION_LONG)) {
			formatter = new HelpFormatter();
			formatter.printHelp("java -jar ECST.jar", options);
		}
	}

	/**
	 * Starts the ECST.
	 */
	public static void main(final String[] args) throws Exception {
		UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		UIManager.put("swing.boldMetal", Boolean.FALSE);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					ECST ecst = new ECST();
					ecst.setVisible(true);
					ecst.parseCommandLine(args);
				} catch (Exception e) {
					// JOptionPane.showMessageDialog(null, e.getMessage(),
					// PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
					e.printStackTrace();
				}
			}
		});
	}
}
