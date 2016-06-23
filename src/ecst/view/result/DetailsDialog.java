package ecst.view.result;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.text.html.HTMLDocument;

import ecst.combiner.CombinerOutputModel;
import ecst.view.ECST;

/**
 * This JDialog shows details about the trained classification system.
 * 
 * @author Matthias Ring
 * 
 */
public class DetailsDialog extends JDialog {

	private static final long serialVersionUID = 1L;

	private JButton okButton;
	private JScrollPane scrollPane;
	private JEditorPane editorPane;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 * @param model
	 */
	public DetailsDialog(ECST ecst, CombinerOutputModel model) {
		setupDialog(ecst);
		fillDialog(model);
	}

	/**
	 * Closes the dialog.
	 * 
	 * @param event
	 */
	public void okButtonActionPerformed(ActionEvent event) {
		dispose();
	}

	/**
	 * Fills the GUI.
	 * 
	 * @param model
	 */
	private void fillDialog(CombinerOutputModel model) {
		StringBuilder text = new StringBuilder();

		text.append("<html><u><font size=\"+1\">Preprocessing model</font></u>");
		text.append("<br><br><pre><font face=\"monospace\">");
		if (model.getPreprocessingAlgorithm() != null) {
			text.append(model.getPreprocessingAlgorithm().getModelString().replaceAll("\n", "<br>"));
		} else {
			text.append("No preprocessing.");
		}
		text.append("</font></pre>");
		text.append("<br><br><u><font size=\"+1\">Feature selection</font></u>");
		text.append("<br><br><pre><font face=\"monospace\">");
		if (model.getFeatureSelectionAlgorithm() != null && model.getFeatureSelectionAlgorithm().getAdditionalInformation() != null) {
			text.append(model.getFeatureSelectionAlgorithm().getAdditionalInformation().replaceAll("\n", "<br>"));
		} else {
			text.append("No feature selection was performed,<br>or the feature selection algorithm<br>does not provide additional information.");
		}
		text.append("</font></pre>");
		text.append("<br><br><u><font size=\"+1\">Classifier model</font></u>");
		text.append("<br><br><pre><font face=\"monospace\">");
		text.append(model.getClassificationAlgorithm().getModelString().replaceAll("\n", "<br>"));
		text.append("</font></pre>");
		text.append("<br><br><u><font size=\"+1\">Evaluation result</font></u>");
		text.append("<br><br><pre><font face=\"monospace\">");
		text.append(model.getEvaluationResult().toSummaryString("", false).replaceAll("\n", "<br>"));
		text.append("<br>");
		try {
			text.append(model.getEvaluationResult().toMatrixString().replaceAll("\n", "<br>"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		text.append("</font></pre>");
		text.append("<br><br><u><font size=\"+1\">Time</font></u>");
		text.append("<br><br><pre><font face=\"monospace\">");
		text.append(("Training and evaluation time: " + model.getTime() + "ms").replaceAll("\n", "<br>"));
		text.append("</font></pre>");
		text.append("</html>");

		editorPane.setText(text.toString());
		editorPane.setCaretPosition(0);
	}

	/**
	 * Creates the GUI.
	 * 
	 * @param ecst
	 */
	private void setupDialog(ECST ecst) {
		Font font = null;
		GridBagConstraints c = null;

		setLayout(new GridBagLayout());

		editorPane = new JEditorPane();
		editorPane.setEditable(false);
		editorPane.setContentType("text/html");
		font = UIManager.getFont("Label.font");
		((HTMLDocument) editorPane.getDocument()).getStyleSheet().addRule(
				"body { font-family: " + font.getFamily() + "; " + "font-size: " + font.getSize() + "pt; }");
		scrollPane = new JScrollPane(editorPane);
		scrollPane.setPreferredSize(new Dimension(500, 300));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.fill = GridBagConstraints.BOTH;
		c.insets = new Insets(10, 10, 10, 10);
		add(scrollPane, c);

		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent event) {
				okButtonActionPerformed(event);
			}
		});
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 10, 10, 10);
		add(okButton, c);

		setTitle("Details");
		setModal(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		pack();
		setLocationRelativeTo(ecst);
	}
}
