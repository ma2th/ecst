package ecst.dataconverter;

/**
 * This class represents a generic pair of two objects.
 * 
 * @author Matthias Ring
 * 
 * @param <A>
 * @param <B>
 */
public class Pair<A, B> {
	private A first;
	private B second;

	/**
	 * Constructor.
	 */
	public Pair(A first, B second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Returns the hash code.
	 */
	public int hashCode() {
		int hashFirst = first != null ? first.hashCode() : 0;
		int hashSecond = second != null ? second.hashCode() : 0;

		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	/**
	 * Equals() implementation.
	 */
	@SuppressWarnings("rawtypes")
	public boolean equals(Object other) {
		if (other instanceof Pair) {
			Pair otherPair = (Pair) other;
			return ((this.first == otherPair.first || (this.first != null && otherPair.first != null && this.first.equals(otherPair.first))) && (this.second == otherPair.second || (this.second != null
					&& otherPair.second != null && this.second.equals(otherPair.second))));
		}

		return false;
	}

	/**
	 * Returns a string describing this object.
	 */
	public String toString() {
		return "(" + first + ", " + second + ")";
	}

	/**
	 * Returns the first element of the pair.
	 * @return
	 */
	public A getFirst() {
		return first;
	}

	/**
	 * Sets the first element in the pair.
	 * @param first
	 */
	public void setFirst(A first) {
		this.first = first;
	}

	/**
	 * Returns the second element of the pair.
	 * @return
	 */
	public B getSecond() {
		return second;
	}

	/**
	 * Sets the second element in the pair.
	 * @param second
	 */
	public void setSecond(B second) {
		this.second = second;
	}
}