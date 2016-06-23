package ecst.view.result;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.text.NumberFormatter;

import ecst.view.ECST;

/**
 * A JDialog to enter upper bound on the complexity analysis.
 * 
 * @author Matthias Ring
 * 
 */
public class FilterDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JButton okButton;
	private JButton cancelButton;
	private Filter result;
	private FilterTableModel tableModel;
	private JFormattedTextField doubleTextField;
	private JFormattedTextField integerTextField;
	private JFormattedTextField totalSpaceTextField;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @param currentFilter
	 */
	public FilterDialog(ECST ecst, Filter currentFilter) {
		setupDialog(ecst);
		if (currentFilter != null) {
			init(currentFilter);
		}
	}

	/**
	 * Initializes the GUI components.
	 * 
	 * @param filter
	 */
	private void init(Filter filter) {
		totalSpaceTextField.setValue(filter.getTotalSpace());
		doubleTextField.setValue(filter.getNumberOfDoubles());
		integerTextField.setValue(filter.getNumberOfIntegers());
		tableModel.init(filter.getMaximumOperations(), filter.getTotalOperations());
	}

	/**
	 * Closes the dialog.
	 * 
	 * @param event
	 */
	public void okButtonActionPerformed(ActionEvent event) {
		result = new Filter(tableModel.getTotalOperations(), (Integer) totalSpaceTextField.getValue(), (Integer) doubleTextField.getValue(),
				(Integer) integerTextField.getValue(), tableModel.getMaximumOperations());
		dispose();
	}

	/**
	 * Cancels the dialog.
	 * 
	 * @param event
	 */
	public void cancelButtonActionPerformed(ActionEvent event) {
		result = null;
		dispose();
	}

	/**
	 * Returns the user settings.
	 * 
	 * @return
	 */
	public Filter getFilterModel() {
		return result;
	}

	/**
	 * Creates a JFormattedTextField for doubles.
	 * 
	 * @return
	 */
	private JFormattedTextField createDoubleTextField() {
		NumberFormatter integerFormatter = null;
		JFormattedTextField formattedTextField = null;

		integerFormatter = new NumberFormatter(NumberFormat.getInstance()) {

			private static final long serialVersionUID = 1L;

			public String valueToString(Object iv) throws ParseException {
				if (iv == null) {
					return "";
				} else {
					return super.valueToString(iv);
				}
			}

			public Object stringToValue(String text) throws ParseException {
				if ("".equals(text)) {
					return null;
				}
				return super.stringToValue(text);
			}
		};

		integerFormatter.setValueClass(Integer.class);
		formattedTextField = new JFormattedTextField(integerFormatter);
		formattedTextField.setPreferredSize(new Dimension(45, 20));

		return formattedTextField;
	}

	/**
	 * Creates the GUI.
	 * 
	 * @param ecst
	 */
	private void setupDialog(ECST ecst) {
		GridBagConstraints c = null;
		JLabel label = null;
		JPanel panel = null;
		JScrollPane pane = null;
		JTable table = null;

		setLayout(new BorderLayout());

		panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

		label = new JLabel("Maximal parameters: ");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		totalSpaceTextField = createDoubleTextField();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(totalSpaceTextField, c);

		label = new JLabel("Maximal integers: ");
		c = new GridBagConstraints();
		c.gridx = 2;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		integerTextField = createDoubleTextField();
		c = new GridBagConstraints();
		c.gridx = 3;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(integerTextField, c);

		label = new JLabel("Maximal floating points: ");
		c = new GridBagConstraints();
		c.gridx = 4;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 5);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(label, c);

		doubleTextField = createDoubleTextField();
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 0;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(doubleTextField, c);

		table = new JTable();
		pane = new JScrollPane(table);
		tableModel = new FilterTableModel(this);
		table.setModel(tableModel);
		pane.setPreferredSize(new Dimension(500, 2 * table.getRowHeight()));
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 6;
		c.weightx = 1.0;
		c.insets = new Insets(10, 0, 5, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		panel.add(pane, c);

		add(panel, BorderLayout.NORTH);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				okButtonActionPerformed(event);
			}
		});

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				cancelButtonActionPerformed(event);

			}
		});

		panel = new JPanel();
		panel.setLayout(new FlowLayout());
		panel.add(okButton);
		panel.add(cancelButton);
		panel.setBorder(BorderFactory.createEmptyBorder(5, 10, 10, 10));

		add(panel, BorderLayout.SOUTH);

		setTitle("Filter analysis");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ecst);
	}

}
