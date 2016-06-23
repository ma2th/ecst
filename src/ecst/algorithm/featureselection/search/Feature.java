package ecst.algorithm.featureselection.search;

/** 
 * This class represents a feature for feature subset selection.
 * 
 * @author Stefan Herpich
 *
 *
 */
public class Feature {
		
	private int feat;
	private double a;
	private int c;
	private String t;
	private int counter;
	private double j;
	
	
	public Feature(int number){
		feat = number;
		a = 0;
		c = 0;
		t = "nothing";
		counter = -1;
		j = Double.NEGATIVE_INFINITY;
	}
	
	public String toString() {
		return "feature: " + feat + ", position: " + counter;
	}
	
	/**
	 * Every feature got his own number in the search tree.
	 * 
	 * @return
	 */
	public int getPos(){
		return counter;
	}
	
	/**
	 * Sets the feature's search tree position.
	 * 
	 * @param c
	 */
	public void setPos(int c){
		counter = c;
	}
	
	/**
	 * Returns the feature's number for identification.
	 * 
	 * @return
	 */
	public int getFeat(){
		return feat;
	}
	
	
	/**
	 * Returns the feature's counter value. For further information see 
	 * the Fast Branch & Bound Algorithm (FBB).
	 * 
	 * @return
	 */
	public int getC(){
		return c;
	}
	
	/**
	 * Sets the feature's counter value.For further information see 
	 * the Fast Branch & Bound Algorithm (FBB).
	 * 
	 * @param counter
	 */
	public void setC(int counter){
		c = counter;
	}
	
	/**
	 * Returns the features contribution value. For further information see
	 * the Fast Branch & Bound Algorithm (FBB).
	 * 
	 * @return
	 */
	public double getA(){
		return a;
	}
	
	/**
	 * Sets the feature's contribution value. For further information see
	 * the Fast Branch & Bound Algorithm (FBB).
	 * 
	 * @param val
	 */
	public void setA(double val){
		a = val;
	}
	
	/**
	 * Sets the feature's result of the criterion function
	 * for a specific path. This property is only used locally
	 * (see BranchAndBound.java).
	 * 
	 * @param comp
	 */
	public void setJ(double comp){
		j = comp;
	}
	
	/**
	 * Returns the feature's result of the criterion function
	 * for a specific path. This property is only used locally
	 * (see BranchAndBound.java).
	 * 
	 * @return
	 */
	public double getJ(){
		return j;
	}
	

	/**
	 * Sets the type of the feature's J calculation.
	 * For further information see
	 * the  Fast Branch & Bound Algorithm (FBB).
	 * This property is only used locally (see BranchAndBound.java)
	 * 
	 * type:= "C" means "computed"
	 * type:= "P" means "predicted"
	 * 
	 * @param type
	 */
	public void setT(String type){
		t = type;
	}
	
	/**
	 * Returns the type of the feature's J calculation.
	 * For further information see 
	 * the Fast Branch & Bound Algorithm (FBB).
	 * This property is only used locally (see BranchAndBound.java)
	 * 
	 * @return
	 */
	
	public String getT(){
		return t;
	}
	
}
