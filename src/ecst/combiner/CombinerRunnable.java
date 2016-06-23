package ecst.combiner;

import java.io.File;

import javax.swing.JOptionPane;

import ecst.io.AnalysisExport;
import ecst.view.ECST;
import ecst.view.result.ResultList;

/**
 * This is a runnable for the Combiner class.
 * 
 * @author Matthias Ring
 * 
 */
public class CombinerRunnable implements Runnable {

	private ECST ecst;
	private Combiner combiner;
	private ProgressDialog dialog;
	private ResultList panel;
	private File exportFile;
	private boolean quitAfterCombiner;

	/**
	 * Constructor.
	 * 
	 * @param combiner
	 * @param listPanel
	 */
	public CombinerRunnable(Combiner combiner, ResultList listPanel, File exportFile, boolean quitAfterCombiner,
			ECST ecst) {
		this.ecst = ecst;
		this.panel = listPanel;
		this.combiner = combiner;
		this.exportFile = exportFile;
		this.quitAfterCombiner = quitAfterCombiner;
	}

	/**
	 * Sets the ProgressDialog.
	 * 
	 * @param dialog
	 */
	public void setDialog(ProgressDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * Executes the Combiner.
	 */
	@Override
	public void run() {
		try {
			combiner.process();
			panel.setCombinerOutputModelList(combiner.getOutputModelList());
			if (exportFile != null) {
				AnalysisExport.exportCSV(combiner.getOutputModelList(), exportFile);
			}

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(panel, e.getMessage(), ECST.PROGRAMM_NAME, JOptionPane.ERROR_MESSAGE);
		} finally {
			dialog.dispose();
			if (quitAfterCombiner) {
				ecst.setVisible(false);
				ecst.dispose();
				System.exit(0);
			}
		}
	}

}
