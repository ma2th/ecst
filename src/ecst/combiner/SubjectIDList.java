package ecst.combiner;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * A list containing the subject IDs for the training data set. The IDs are
 * removed at the beginning, because WEKA currently cannot handle subject IDs.
 * The IDs are saved in this list according to the ordering of the orginal data.
 * 
 * @author Matthias Ring
 * 
 */
public class SubjectIDList extends LinkedList<Integer> {

	private static final long serialVersionUID = 1L;

	/**
	 * Returns a set containing every ID exactly once.
	 * 
	 * @return
	 */
	public Set<Integer> getUniqueIDs() {
		Set<Integer> result = new HashSet<Integer>();

		for (Integer subjectID : this) {
			result.add(subjectID);
		}
		return result;
	}

	/**
	 * Returns the indices in this list for the given subject ID.
	 * 
	 * @param id
	 * @return
	 */
	public List<Integer> getIndicesForID(Integer id) {
		List<Integer> result = new LinkedList<Integer>();

		for (int i = 0; i < this.size(); i++) {
			if (get(i).equals(id)) {
				result.add(i);
			}
		}
		return result;
	}

}
