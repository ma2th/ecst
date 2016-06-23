package ecst.combiner;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;

import ecst.view.ECST;

/**
 * This JDialog shows the progress of the training phase.
 * 
 * @author Matthias Ring
 * 
 */
public class ProgressDialog extends JDialog implements ProgressListener {

	private static final long serialVersionUID = 1L;

	private JLabel infoLabel;
	private Combiner combiner;
	private JButton cancelButton;
	private JProgressBar progressBar;
	private JLayeredPane pane;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 */
	public ProgressDialog(ECST ecst) {
		setupDialog(ecst);
	}

	/**
	 * The Combiner object that trains the classification systems.
	 * 
	 * @param combiner
	 */
	public void setCombiner(Combiner combiner) {
		this.combiner = combiner;
	}

	/**
	 * Handles the event if the user pressed the cancel button.
	 * 
	 * @param event
	 */
	public void cancelButtonActionPerformed(ActionEvent event) {
		cancelButton.setEnabled(false);
		cancelButton.setText("Canceling...");
		combiner.pleaseStop();
	}

	/**
	 * This method is called by the Combiner object to tell that progress has
	 * been made.
	 */
	@Override
	public void progressMade(ProgressEvent event) {
		if (event.getProgress() == -1) {
			dispose();
		}
		progressBar.setValue(event.getProgress());
		infoLabel.setText(event.getDescription());
		pane.moveToFront(infoLabel);
	}

	/**
	 * Sets the GUI up.
	 * 
	 * @param ecst
	 */
	private void setupDialog(ECST ecst) {
		GridBagConstraints c = null;
		pane = new JLayeredPane();

		setLayout(new BorderLayout());
		add(pane, BorderLayout.CENTER);

		pane.setLayout(new GridBagLayout());
		pane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		progressBar = new JProgressBar();
		progressBar.setPreferredSize(new Dimension(200, 25));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		pane.add(progressBar, c, 1);

		infoLabel = new JLabel();
		infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		pane.add(infoLabel, c, 0);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				cancelButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(10, 0, 0, 0);
		pane.add(cancelButton, c);

		setTitle("Procressing pipeline combinations");
		setSize(new Dimension(610, 105));
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(ecst);
	}

}
