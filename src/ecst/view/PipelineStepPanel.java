package ecst.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import ecst.algorithm.Algorithm;
import ecst.algorithm.definition.AlgorithmDefinition;
import ecst.view.selectiontable.CreateNewTab;
import ecst.view.selectiontable.InstanceTab;
import ecst.view.selectiontable.SelectionTable;

/**
 * This JPanel represents a step in the pattern recognition pipeline.
 * 
 * @author Matthias Ring
 * 
 */
public class PipelineStepPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private SelectionTable table;
	private JLabel algorithmLabel;
	private JTextPane descriptionPane;
	private JTabbedPane tabbedPane;

	/**
	 * Constructor.
	 * 
	 * @param algorithmDefinitions
	 * @param addNull
	 * @throws Exception
	 */
	public PipelineStepPanel(List<AlgorithmDefinition> algorithmDefinitions, boolean addNull) throws Exception {
		setupPanel();
		setAlgorithms(createAllAlgorithmsList(algorithmDefinitions, addNull));
	}

	/**
	 * Fills the GUI with the given algorithms.
	 * 
	 * @param allAlgorithms
	 * @throws Exception
	 */
	public void setAlgorithms(List<List<Algorithm>> allAlgorithms) throws Exception {
		table.init(allAlgorithms);
		table.getModel().addTableModelListener(new TableModelListener() {
			@Override
			public void tableChanged(TableModelEvent event) {
				tableModelChanged(event);

			}
		});
		table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event) {
				tableSelectionChanged(event);
			}
		});
		table.setFirstRowSelected();
	}

	/**
	 * Sets the selected algorithms.
	 * 
	 * @param selectedAlgorithms
	 */
	public void setSelectedAlgorithms(List<Boolean> selectedAlgorithms) {
		table.getModel().setSelectedAlgorithms(selectedAlgorithms);
	}

	/**
	 * Returns all algorithms.
	 * 
	 * @return
	 */
	public List<Algorithm> getAlgorithms() {
		List<Algorithm> result = new LinkedList<Algorithm>();

		for (int i = 0; i < table.getModel().getAllAlgorithms().size(); i++) {
			if (table.getModel().getAllAlgorithms().get(i) == null) {
				result.add(null);
			} else {
				result.addAll(table.getModel().getAllAlgorithms().get(i));
			}
		}
		return result;
	}

	/**
	 * Returns the selected algorithms.
	 * 
	 * @return
	 */
	public List<Algorithm> getSelectedAlgorithms() {
		Algorithm algorithm = null;
		List<Algorithm> result = new LinkedList<Algorithm>();
		List<Boolean> selectedAlgorithms = table.getModel().getSelectedAlgorithms();

		for (int i = 0; i < selectedAlgorithms.size(); i++) {
			if (selectedAlgorithms.get(i)) {
				if (table.getModel().getAllAlgorithms().get(i) == null) {
					result.add(null);
				} else {
					if (table.getModel().getAllAlgorithms().get(i).size() > 1) {
						for (int j = 0; j < table.getModel().getAllAlgorithms().get(i).size(); j++) {
							algorithm = table.getModel().getAllAlgorithms().get(i).get(j);
							algorithm.setInstanceCounter(j + 1);
							result.add(algorithm);
						}
					} else {
						result.addAll(table.getModel().getAllAlgorithms().get(i));
					}
				}
			}
		}
		return result;
	}

	/**
	 * Returns all algorithms.
	 * 
	 * @param algorithmDefinitions
	 * @param addNull
	 * @return
	 * @throws Exception
	 */
	private List<List<Algorithm>> createAllAlgorithmsList(List<AlgorithmDefinition> algorithmDefinitions, boolean addNull) throws Exception {
		List<Algorithm> algorithms = null;
		List<List<Algorithm>> allAlgorithms = null;

		allAlgorithms = new LinkedList<List<Algorithm>>();
		if (algorithmDefinitions != null) {
			for (AlgorithmDefinition definition : algorithmDefinitions) {
				algorithms = new LinkedList<Algorithm>();
				algorithms.add(definition.createInstance());
				allAlgorithms.add(algorithms);
			}
		}
		if (addNull) {
			allAlgorithms.add(null);
		}
		return allAlgorithms;
	}

	/**
	 * Updates the content of the text field describing the algorithm.
	 * 
	 * @param selectedRow
	 */
	private void updateInfo(int selectedRow) {
		Method method = null;
		String description = null;
		Algorithm algorithm = null;
		List<Algorithm> algorithms = null;

		try {
			algorithms = table.getModel().getAlgorithms(selectedRow);
			if (algorithms != null && algorithms.size() > 0) {
				algorithm = algorithms.get(0);
				algorithmLabel.setText(algorithm.getDefinition().getName());

				method = algorithm.getImplementingClass().getDeclaredMethod("globalInfo");
				description = (String) method.invoke(algorithm.getImplementingClass().newInstance());
				descriptionPane.setText(description);
				descriptionPane.setCaretPosition(0);
			} else {
				tabbedPane.removeAll();
				algorithmLabel.setText("");
				descriptionPane.setText("");
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	/**
	 * Updates the GUI if another algorithm was selected.
	 * 
	 * @param selectedRow
	 */
	private void updateTabbedPane(int selectedRow) {
		int i = 0;
		BoxLayout boxLayout = null;
		Algorithm algorithm = null;
		JPanel detailsPanel = null;
		JScrollPane scrollPane = null;
		List<Algorithm> algorithms = null;

		tabbedPane.removeAll();
		algorithms = table.getModel().getAlgorithms(selectedRow);
		for (i = 0; i < algorithms.size(); i++) {
			algorithm = algorithms.get(i);

			scrollPane = new JScrollPane();
			scrollPane.setBorder(null);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scrollPane.setPreferredSize(new Dimension(400, 250));

			detailsPanel = new JPanel();
			boxLayout = new BoxLayout(detailsPanel, BoxLayout.Y_AXIS);
			detailsPanel.setLayout(boxLayout);
			detailsPanel.add(algorithm.getEditor());
			detailsPanel.add(Box.createVerticalGlue());

			algorithm.getEditor().setPreferredSize(new Dimension(scrollPane.getPreferredSize().width - 5, algorithm.getEditor().getPreferredSize().height));
			scrollPane.setViewportView(detailsPanel);
			scrollPane.revalidate();

			tabbedPane.addTab("Instance " + (i + 1), scrollPane);
			tabbedPane.setTabComponentAt(i, new InstanceTab(tabbedPane, table.getModel(), selectedRow));
		}
		tabbedPane.addTab("+", new JPanel());
		tabbedPane.setTabComponentAt(i, new CreateNewTab(tabbedPane, table.getModel(), selectedRow));
		tabbedPane.revalidate();
		tabbedPane.repaint();
	}

	/**
	 * Table selection changed event -> update GUI.
	 * 
	 * @param event
	 */
	public void tableSelectionChanged(ListSelectionEvent event) {
		int selectedRow = table.getSelectedRow();

		if (selectedRow == -1) {
			return;
		} else {
			if (table.getModel().getAlgorithms(selectedRow) != null) {
				updateTabbedPane(selectedRow);
			}
			updateInfo(selectedRow);
		}
	}

	/**
	 * Table model changed event -> update GUI.
	 * 
	 * @param event
	 */
	public void tableModelChanged(TableModelEvent event) {
		int selectedRow = table.getSelectedRow();

		if (selectedRow == -1) {
			return;
		} else {
			if (table.getModel().getAlgorithms(selectedRow) != null) {
				updateTabbedPane(selectedRow);
			}
		}
	}

	/**
	 * Creates the GUI elements.
	 */
	private void setupPanel() {
		JPanel panel = null;
		GridBagConstraints c = null;
		JScrollPane scrollPane = null;
		BorderLayout borderLayout = null;

		setLayout(new GridBagLayout());

		table = new SelectionTable();
		scrollPane = new JScrollPane(table);
		panel = new JPanel();
		panel.setLayout(new BorderLayout());
		panel.add(scrollPane, BorderLayout.CENTER);
		panel.setPreferredSize(new Dimension(210, 250));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 0.2;
		c.weighty = 1.0;
		c.gridheight = 2;
		c.fill = GridBagConstraints.BOTH;
		add(panel, c);

		panel = new JPanel();
		borderLayout = new BorderLayout();
		borderLayout.setVgap(15);
		panel.setLayout(borderLayout);
		algorithmLabel = new JLabel();
		algorithmLabel.setFont(algorithmLabel.getFont().deriveFont(22f));
		panel.add(algorithmLabel, BorderLayout.NORTH);
		descriptionPane = new JTextPane();
		descriptionPane.setBackground(Color.WHITE);
		descriptionPane.setEditable(false);
		descriptionPane.setFont(descriptionPane.getFont().deriveFont(11f));
		descriptionPane.setBorder(null);
		scrollPane = new JScrollPane(descriptionPane);
		scrollPane.setPreferredSize(new Dimension(350, 60));
		panel.add(scrollPane, BorderLayout.CENTER);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.weightx = 0.8;
		c.weighty = 0.3;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(0, 17, 0, 2);
		add(panel, c);

		tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.weightx = 0.8;
		c.weighty = 1.0;
		c.weighty = 0.7;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(8, 15, 0, 0);
		add(tabbedPane, c);

		setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
	}

}
