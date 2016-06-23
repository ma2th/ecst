package ecst.view.selectiontable;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.plaf.basic.BasicButtonUI;

import ecst.view.ECST;

/**
 * A special JPanel to show a add button in the tabbed pane.
 * 
 * @author Matthias Ring
 * 
 */
public class CreateNewTab extends JPanel {
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
	public CreateNewTab(JTabbedPane pane, SelectionTableModel model, int tableRow) {
		this.pane = pane;
		this.model = model;
		this.tableRow = tableRow;
		setupPanel();
	}

	/**
	 * Creates the GUI.
	 */
	private void setupPanel() {
		JButton button = null;

		setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
		setOpaque(false);

		button = new JButton("<html><font size=\"-2\">\uFF0B</font></html>");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				int i = pane.indexOfTabComponent(CreateNewTab.this);
				if (i != -1) {
					try {
						model.addAlgorithm(tableRow, i);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(pane, "Could not create new algorithm!", ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
						e.printStackTrace();
					}
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
