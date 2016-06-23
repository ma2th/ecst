package ecst.view.result;

import java.awt.BorderLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ecst.algorithm.analysis.Analysis;
import ecst.algorithm.definition.OperationDefinition;
import ecst.combiner.CombinerOutputModel;
import ecst.utilities.MathUtilities;
import ecst.view.ECST;

/**
 * This JPanel displays the complexity analysis.
 * 
 * @author Matthias Ring
 * 
 */
public class ResultList extends JPanel {

	private static final long serialVersionUID = 1L;

	private ECST ecst;
	private JList list;
	private Filter filter;
	private JScrollPane scrollPane;
	private DefaultListModel model;
	private List<CombinerOutputModel> outputModelList;

	/**
	 * Constructor.
	 * 
	 * @param ecst
	 */
	public ResultList(ECST ecst) {
		this.ecst = ecst;

		setupPanel();
	}

	/**
	 * Returns the currently used filter object.
	 * 
	 * @return
	 */
	public Filter getCurrentFilter() {
		return filter;
	}

	/**
	 * Returns the data to be displayed.
	 * 
	 * @return
	 */
	public List<CombinerOutputModel> getModel() {
		return outputModelList;
	}

	/**
	 * Sets the data to be displayed.
	 * 
	 * @param outputModelList
	 */
	public void setCombinerOutputModelList(List<CombinerOutputModel> outputModelList) {
		Collections.sort(outputModelList);

		model.clear();
		for (CombinerOutputModel pipeline : outputModelList) {
			model.addElement(pipeline);
		}

		this.outputModelList = outputModelList;
	}

	/**
	 * Applies a filter to the result list.
	 * 
	 * @param filter
	 */
	public void setFilter(Filter filter) {
		model.clear();

		for (CombinerOutputModel outputModel : outputModelList) {
			if (checkFilter(filter, outputModel)) {
				model.addElement(outputModel);
			}
		}

		this.filter = filter;
	}

	/**
	 * Internal method to decide if a classification system fulfills the filter
	 * criterions.
	 * 
	 * @param filter
	 * @param outputModel
	 * @return
	 */
	private boolean checkFilter(Filter filter, CombinerOutputModel outputModel) {
		Integer value = null;
		Analysis analyis = new Analysis(outputModel);

		if (filter.getTotalOperations() != null) {
			if (analyis.getTotalOperations() > filter.getTotalOperations()) {
				return false;
			}
		}
		if (filter.getTotalSpace() != null) {
			if (analyis.getTotalSpace() > filter.getTotalSpace()) {
				return false;
			}
		}
		if (filter.getNumberOfIntegers() != null) {
			if (MathUtilities.sumIntArray(analyis.getNumberOfIntegers()) > filter.getNumberOfIntegers()) {
				return false;
			}
		}
		if (filter.getNumberOfDoubles() != null) {
			if (MathUtilities.sumIntArray(analyis.getNumberOfFloats()) > filter.getNumberOfDoubles()) {
				return false;
			}
		}

		for (OperationDefinition definition : filter.getMaximumOperations().keySet()) {
			value = filter.getMaximumOperations().get(definition);
			if (value != null) {
				if (MathUtilities.sumIntArray(analyis.getOperations(definition)) > value) {
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * Creates the GUI elements.
	 */
	private void setupPanel() {
		setLayout(new BorderLayout());

		list = new JList();
		list.addMouseListener(new ListMouseListener(ecst, list));
		list.setToolTipText("Double click for details");
		model = new DefaultListModel();
		list.setModel(model);
		list.setCellRenderer(new ListRenderer());
		scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);
	}

}
