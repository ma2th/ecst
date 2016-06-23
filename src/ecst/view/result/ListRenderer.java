package ecst.view.result;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListCellRenderer;
import javax.swing.UIManager;

import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.Analysis;
import ecst.combiner.CombinerOutputModel;
import ecst.combiner.PipelineData;
import ecst.utilities.CommonUtilities;
import weka.core.Attribute;
import weka.core.Instances;

/**
 * ListCellRenderer to display the complexity analysis in the result panel.
 * 
 * @author Matthias Ring
 * 
 */
public class ListRenderer extends JPanel implements ListCellRenderer {

	private static final long serialVersionUID = 1L;

	private JLabel numberOfInstances;
	private JLabel selectedAttributes;
	private JLabel descriptionLabel;
	private JLabel classificationLabel;
	private JLabel totalOperations;
	private JLabel spaceLabel;
	private JLabel gridSearchDescLabel;
	private JLabel gridSearchLabel;
	private JLabel tpLabel;
	private JTable table;
	private OperationsTableModel tableModel;

	/**
	 * Constructor.
	 */
	public ListRenderer() {
		super();
		setupLayout();
	}

	/**
	 * Returns a panel that displays the complexity analysis.
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		ClassificationAlgorithm classificationAlgorithm = null;
		CombinerOutputModel outputModel = (CombinerOutputModel) value;
		NumberFormat format = DecimalFormat.getInstance();
		Analysis analysis = new Analysis(outputModel);
		String tmp = null;

		descriptionLabel.setText(buildPipelineDescription(outputModel));
		classificationLabel.setText(format.format(outputModel.getEvaluationResult().pctCorrect()) + "%");
		tpLabel.setText(CommonUtilities.createTPRateString(outputModel, format, true));
		spaceLabel.setText("<html>" + analysis.getTotalSpace() + " parameters: "
				+ CommonUtilities.createComplexityDetailsStringHTML(analysis.getNumberOfFloats())
				+ " floating point and "
				+ CommonUtilities.createComplexityDetailsStringHTML(analysis.getNumberOfIntegers())
				+ " integer</html>");
		tableModel.setTrainedPipeline(analysis);
		numberOfInstances.setText(outputModel.getData().getPreprocessedInstances().numInstances() + "/"
				+ outputModel.getData().getInstances().numInstances());
		selectedAttributes.setText(buildSelectedAttributesString(outputModel.getData()));
		totalOperations.setText("(" + analysis.getTotalOperations() + " total)");

		classificationAlgorithm = outputModel.getClassificationAlgorithm();
		if ((Boolean) classificationAlgorithm.isGridSearch()) {
			tmp = classificationAlgorithm.getGridSearchXName();
			tmp += ": " + classificationAlgorithm.getGridSearchXValue();
			tmp += ", " + classificationAlgorithm.getGridSearchYName();
			tmp += ": " + classificationAlgorithm.getGridSearchYValue();
			gridSearchLabel.setText("<html>" + tmp + "</html>");
			gridSearchDescLabel.setVisible(true);
			gridSearchLabel.setVisible(true);
		} else {
			gridSearchDescLabel.setVisible(false);
			gridSearchLabel.setVisible(false);
		}

		if (isSelected) {
			setBackground(UIManager.getColor("List.selectionBackground"));
		} else {
			setBackground(Color.WHITE);
		}
		revalidate();

		return this;
	}

		/**
	 * Creates a HTML string that describes the selected attributes.
	 * 
	 * @param data
	 * @return
	 */
	private String buildSelectedAttributesString(PipelineData data) {
		final int LINE_LENGTH = 100;
		String result = "<html>";

		result += data.getAttributesAfterTraining().size() + "/" + (data.getInstances().numAttributes() - 1);
		if (data.getPreprocessedInstances().numAttributes() > data.getInstances().numAttributes()) {
			result += "+" + (data.getPreprocessedInstances().numAttributes() - 1);
		}
		result += "; ";

		for (int i = 0; i < data.getInstances().numAttributes() - 1; i++) {
			result += buildAttributeString(data.getInstances(), data.getAttributesAfterTraining(), i, true);
			if ((result.lastIndexOf("<br>") == -1 && result.length() > LINE_LENGTH) || (result.lastIndexOf("<br>") > -1
					&& result.substring(result.lastIndexOf("<br>"), result.length()).length() > LINE_LENGTH)) {
				result += "<br>";
			}
		}
		if (data.getPreprocessedInstances().numAttributes() > data.getInstances().numAttributes()) {
			result += ", ";
			for (int i = 0; i < data.getPreprocessedInstances().numAttributes() - 1; i++) {
				result += buildAttributeString(data.getPreprocessedInstances(), data.getAttributesAfterTraining(), i,
						false);
				if ((result.lastIndexOf("<br>") == -1 && result.length() > LINE_LENGTH)
						|| (result.lastIndexOf("<br>") > -1 && result
								.substring(result.lastIndexOf("<br>"), result.length()).length() > LINE_LENGTH)) {
					result += "<br>";
				}
			}
		}

		return result + "</html>";
	}

	/**
	 * Builds the HTML String for one attribute.
	 * 
	 * @param instances
	 * @param attributesAfterTraining
	 * @param attributeIndex
	 * @return
	 */
	private String buildAttributeString(Instances instances, List<String> attributesAfterTraining, int attributeIndex,
			boolean showNonSelected) {
		String returnValue = "";
		Attribute trainingAttribute = null;

		trainingAttribute = instances.attribute(attributeIndex);
		if (attributesAfterTraining.contains(trainingAttribute.name())) {
			returnValue += trainingAttribute.name();
			if (attributeIndex < instances.numAttributes() - 2) {
				returnValue += ", ";
			}
		} else {
			if (showNonSelected) {
				returnValue += "<s>" + trainingAttribute.name() + "</s>";
				if (attributeIndex < instances.numAttributes() - 2) {
					returnValue += ", ";
				}
			}
		}

		return returnValue;
	}

	/**
	 * Creates a HTML string that describes the classification system.
	 * 
	 * @param outputModel
	 * @return
	 */
	private String buildPipelineDescription(CombinerOutputModel outputModel) {
		String resultString = "<html>";

		if (outputModel.getPreprocessingAlgorithm() != null) {
			resultString += CommonUtilities
					.createAlgorithmNameWithInstanceCounter(outputModel.getPreprocessingAlgorithm(), true) + " &rarr; ";
		}
		if (outputModel.getFeatureSelectionAlgorithm() != null) {
			resultString += CommonUtilities.createAlgorithmNameWithInstanceCounter(
					outputModel.getFeatureSelectionAlgorithm(), true) + " &rarr; ";
		}

		return resultString
				+ CommonUtilities.createAlgorithmNameWithInstanceCounter(outputModel.getClassificationAlgorithm(), true)
				+ " &rarr; "
				+ CommonUtilities.createAlgorithmNameWithInstanceCounter(outputModel.getEvaluationAlgorithm(), true)
				+ "</html>";
	}

	/**
	 * Creates the GUI.
	 */
	private void setupLayout() {
		GridBagConstraints c = null;
		JScrollPane pane = null;
		JLabel label = null;

		setLayout(new GridBagLayout());
		setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		descriptionLabel = new JLabel();
		descriptionLabel.setFont(descriptionLabel.getFont().deriveFont(14f));
		descriptionLabel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Color.black));
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 0;
		c.weightx = 1.0;
		c.gridwidth = 2;
		c.anchor = GridBagConstraints.CENTER;
		c.insets = new Insets(0, 0, 10, 0);
		c.fill = GridBagConstraints.HORIZONTAL;
		add(descriptionLabel, c);

		label = new JLabel("Accuracy:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 1;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		classificationLabel = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 1;
		c.anchor = GridBagConstraints.LINE_START;
		add(classificationLabel, c);

		label = new JLabel("TP Rate:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 2;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		tpLabel = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 2;
		c.anchor = GridBagConstraints.LINE_START;
		add(tpLabel, c);

		label = new JLabel("Instances:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 3;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		numberOfInstances = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 3;
		c.anchor = GridBagConstraints.LINE_START;
		add(numberOfInstances, c);

		label = new JLabel("Attributes:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 4;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		selectedAttributes = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 4;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(selectedAttributes, c);

		gridSearchDescLabel = new JLabel("Grid search:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 5;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(gridSearchDescLabel, c);

		gridSearchLabel = new JLabel("-");
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 5;
		c.anchor = GridBagConstraints.LINE_START;
		add(gridSearchLabel, c);

		label = new JLabel("Memory:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 6;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_START;
		add(label, c);

		spaceLabel = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 6;
		c.anchor = GridBagConstraints.LINE_START;
		add(spaceLabel, c);

		label = new JLabel("Operations:");
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 7;
		c.insets = new Insets(0, 0, 0, 10);
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		add(label, c);

		totalOperations = new JLabel();
		c = new GridBagConstraints();
		c.gridx = 0;
		c.gridy = 8;
		c.insets = new Insets(0, 0, 5, 10);
		c.anchor = GridBagConstraints.LINE_END;
		add(totalOperations, c);

		table = new JTable();
		pane = new JScrollPane(table);
		tableModel = new OperationsTableModel();
		table.setModel(tableModel);
		table.setRowHeight(table.getRowHeight() + 4);
		pane.setPreferredSize(new Dimension(500, 2 * table.getRowHeight()));
		pane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
		c = new GridBagConstraints();
		c.gridx = 1;
		c.gridy = 7;
		c.gridheight = 2;
		c.weightx = 1.0;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.LINE_START;
		add(pane, c);
	}

}
