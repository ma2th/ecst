package ecst.combiner;

import java.util.EventObject;

/**
 * An event fired by the Combiner to tell the Dialog that progress has been
 * made.
 * 
 * @author Matthias Ring
 * 
 */
public class ProgressEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	private int progress;
	private String description;

	/**
	 * Constructor.
	 */
	public ProgressEvent(Object source, int progress, String description) {
		super(source);
		this.progress = progress;
		this.description = description;
	}

	/**
	 * Returns the progress in percent.
	 * 
	 * @return
	 */
	public int getProgress() {
		return progress;
	}

	/**
	 * Returns a string describing the current classification system.
	 * 
	 * @return
	 */
	public String getDescription() {
		return description;
	}

}
