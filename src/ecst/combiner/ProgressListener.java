package ecst.combiner;

import java.util.EventListener;

/**
 * Interface for progress listeners of the Combiner object.
 * 
 * @author Matthias Ring
 * 
 */
public interface ProgressListener extends EventListener {

	/**
	 * The Combiner calls this method if progress has been made.
	 * 
	 * @param event
	 */
	public void progressMade(ProgressEvent event);

}
