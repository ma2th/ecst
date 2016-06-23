package ecst.view.selectiontable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

/**
 * A special JPanel to display a delete button in the header of the tabbed pane.
 * 
 * @author Matthias Ring
 * 
 */
public class InstanceTab extends JPanel {

	private static final long serialVersionUID = 1L;

	private int tableRow;
	private JTabbedPane pane;
	private SelectionTableModel model;

	/**
	 * Constructor.
	 * 
	 * @param pane
	 * @param model
	 * @param tableRow
	 */
	public InstanceTab(JTabbedPane pane, SelectionTableModel model, int tableRow) {
		this.pane = pane;
		this.model = model;
		this.tableRow = tableRow;
		setupPanel();
	}

	/**
	 * Creates a GUI.
	 */
	private void setupPanel() {
		JLabel label = null;
		JButton button = null;

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);

		label = new JLabel() {
			private static final long serialVersionUID = 1L;

			public String getText() {
				int i = pane.indexOfTabComponent(InstanceTab.this);
				if (i != -1) {
					return pane.getTitleAt(i);
				}
				return null;
			}
		};
		label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
		add(label);

		button = new JButton("<html><font size=\"-2\">\u2573</font></html>");
		button.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				int i = pane.indexOfTabComponent(InstanceTab.this);
				if (i > -1 && pane.getTabCount() > 2) {
					model.removeAlgorithm(tableRow, i);
				}
			}
		});
		button.addMouseListener(new MouseListener() {
			public void mouseEntered(MouseEvent e) {
				JButton button = (JButton) e.getComponent();
				button.setBorderPainted(true);
			}

			public void mouseExited(MouseEvent e) {
				JButton button = (JButton) e.getComponent();
				button.setBorderPainted(false);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

		});
		button.setPreferredSize(new Dimension(14, 14));
		button.setUI(new BasicButtonUI());
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setBorder(BorderFactory.createEtchedBorder());
		add(button);

		setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
	}
}
