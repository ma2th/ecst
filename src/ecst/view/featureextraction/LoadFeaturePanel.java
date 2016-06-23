package ecst.view.featureextraction;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ecst.utilities.FileUtilities;
import ecst.view.ECST;

/**
 * This JPanel shows the possibilities to select a file with pre-computed
 * features.
 * 
 * @author Matthias Ring
 * 
 */
public class LoadFeaturePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private JButton selectButton;
	private JTextField filenameTextField;
	private ECST ecst;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 */
	public LoadFeaturePanel(ECST ecst) {
		this.ecst = ecst;

		setupPanel();
		filenameTextField.setText(ecst.getProperties().getProperty("ecst.view.featureextraction.LoadFeaturePanel.filenameTextField"));
	}

	/**
	 * Sets the filename.
	 * 
	 * @param filename
	 */
	public void setFilename(String filename) {
		filenameTextField.setText(filename);
		ecst.getProperties().setProperty("ecst.view.featureextraction.LoadFeaturePanel.filenameTextField", filename);
	}

	/**
	 * Returns the filename.
	 * 
	 * @return
	 */
	public String getFilename() {
		return filenameTextField.getText();
	}

	/**
	 * Shows a JFileChooser to select the file.
	 * 
	 * @param event
	 */
	public void selectButtonActionPerformed(ActionEvent event) {
		File selectedFile = null;

		if ((selectedFile = FileUtilities.showOpenFileChooserARFF(this, getFilename())) != null) {
			setFilename(selectedFile.getAbsolutePath());
		}
	}

	/**
	 * Creates the GUI.
	 */
	private void setupPanel() {
		GridBagConstraints c = null;

		setLayout(new GridBagLayout());

		filenameTextField = new JTextField();
		filenameTextField.setPreferredSize(new Dimension(400, (int) filenameTextField.getPreferredSize().getHeight()));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 0, 2);
		add(filenameTextField, c);

		selectButton = new JButton();
		selectButton.setText("Choose...");
		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				selectButtonActionPerformed(event);
			}

		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		add(selectButton, c);
	}

}
