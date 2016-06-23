package ecst.view.featureextraction;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;

import ecst.algorithm.Algorithm;
import ecst.algorithm.FeatureExtractionAlgorithm;
import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.AlgorithmDefinition;
import ecst.view.ECST;

/**
 * A JDialog to select a feature extraction algorithm.
 * 
 * @author Matthias Ring
 * 
 */
public class FeatureSelectionDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JButton okButton;
	private JButton cancelButton;
	private JComboBox algorithmComboBox;
	private FeatureExtractionAlgorithm algorithm;
	private JScrollPane panelScrollPane;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @throws Exception
	 */
	public FeatureSelectionDialog(ECST ecst) throws Exception {
		setupDialog(ecst);
		initAlgorithms();
	}

	/**
	 * Returns the selected algorithm.
	 * 
	 * @return
	 */
	public FeatureExtractionAlgorithm getAlgorithm() {
		return algorithm;
	}

	/**
	 * Closes the dialog.
	 * 
	 * @param event
	 */
	public void okButtonActionPerformed(ActionEvent event) {
		algorithm = (FeatureExtractionAlgorithm) algorithmComboBox.getSelectedItem();
		algorithm.readEditorSettings();
		dispose();
	}

	/**
	 * Cancels the dialog.
	 * 
	 * @param event
	 */
	public void cancelButtonActionPerformed(ActionEvent event) {
		dispose();
	}

	/**
	 * Updates the parameter view if the algorithm selection changed.
	 * 
	 * @param event
	 */
	public void algorithmComboBoxItemChanged(ItemEvent event) {
		JPanel detailsPanel = new JPanel();
		Algorithm algorithm = (Algorithm) algorithmComboBox.getSelectedItem();

		detailsPanel.setLayout(new BorderLayout());
		if (algorithm != null) {
			detailsPanel.add(algorithm.getEditor(), BorderLayout.NORTH);
			algorithm.getEditor()
					.setPreferredSize(new Dimension(panelScrollPane.getPreferredSize().width - 7, algorithm.getEditor().getPreferredSize().height));
		}
		panelScrollPane.setViewportView(detailsPanel);
		panelScrollPane.revalidate();
		panelScrollPane.repaint();
	}

	/**
	 * Initializes the selection box.
	 * 
	 * @throws Exception
	 */
	private void initAlgorithms() throws Exception {
		for (AlgorithmDefinition definition : AlgorithmBox.getInstance().getFeatureExtractionAlgorithms()) {
			algorithmComboBox.addItem(definition.createInstance());
		}
	}

	/**
	 * Creates the GUI components.
	 * 
	 * @param ecst
	 */
	private void setupDialog(ECST ecst) {
		JLabel label = null;
		JPanel panel = null;
		JPanel tmpPanel = null;
		GridBagConstraints c = null;

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());

		label = new JLabel("Select feature:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 10, 5);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		algorithmComboBox = new JComboBox();
		algorithmComboBox.setPreferredSize(new Dimension(200, 20));
		algorithmComboBox.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				algorithmComboBoxItemChanged(event);
			}
		});
		algorithmComboBox.setRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList list, Object object, int index, boolean isSelected, boolean cellHasFocus) {
				return super.getListCellRendererComponent(list, object.toString().substring(0, (object.toString()).indexOf("of column")), index, isSelected,
						cellHasFocus);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 10, 0);
		panel.add(algorithmComboBox, c);

		panelScrollPane = new JScrollPane();
		panelScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		panelScrollPane.setPreferredSize(new Dimension(300, 92));
		panelScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Parameter", TitledBorder.LEFT, TitledBorder.TOP));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.weightx = 1.0;
		c.gridwidth = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.insets = new Insets(0, 0, 10, 0);
		panel.add(panelScrollPane, c);

		tmpPanel = new JPanel();
		tmpPanel.setPreferredSize(new Dimension(10, 1));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tmpPanel, c);

		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				okButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 0, 5);
		panel.add(okButton, c);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				cancelButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 2;
		panel.add(cancelButton, c);

		tmpPanel = new JPanel();
		tmpPanel.setPreferredSize(new Dimension(10, 1));
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(tmpPanel, c);

		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		setLayout(new BorderLayout());
		add(panel, BorderLayout.CENTER);
		setTitle("Add feature");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ecst);
	}
}
