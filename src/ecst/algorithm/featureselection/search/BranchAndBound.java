package ecst.algorithm.featureselection.search;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.SubsetEvaluator;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import ecst.utilities.CommonUtilities;
import ecst.utilities.MathUtilities;

/**
 * This class implements different branch-and-bound search strategies according
 * to
 * 
 * M. Ring, B. M. Eskofier, Optimal Feature Selection in Nonlinear Data Using
 * Branch-and-Bound in Kernel Space, submitted for publication on Sep. 23, 2013.
 * 
 * and
 * 
 * Xue-wen Chen, An improved branch and bound algorithm for feature selection,
 * Pattern Recognition Letters 24(12):1925 - 1933, 2003.
 * 
 * and
 * 
 * Patrenahalli M. Narendra, Keinosuke Fukunaga, A Branch and Bound Algorithm
 * for Feature Subset Selection, IEEE Transactions on Computers, Vol. C-26,
 * No.9, September 1977.
 * 
 * and
 * 
 * Petr Somol, Pavel Pudil, Josef Kittler, Fast Branch & Bound Algorithms for
 * Optimal Feature Selection, IEEE Transactions on Pattern Analysis and Machine
 * Intelligence, Vol. 26, No. 7, July 2004.
 * 
 * and
 * 
 * Songyot Nakariyakul, David P. Casasent, Adaptive branch and bound algorithm
 * for selecting optimale features, Pattern Recognition Letters 28 (2007).
 * 
 * and
 * 
 * Zhenxiao Wang, Jie Yang, Guozheng Li, An Improved Branch & Bound Algorithm in
 * Feature Selection, G. Wang et al. (Eds.): RSFDGrC 2003, LNAI 2639, pp.
 * 549-556, 2003.
 * 
 * and
 * 
 * P. Pudil, J. Novovosov, J. Kittler, Floating search methods in feature
 * selection, Pattern Recognition Letters 15 (1994).
 * 
 * and
 * 
 * Songyot Nakariyakul, Study on Criterion Function Models in the Adaptive
 * Branch and Bound Algorithm, 2009 Eighth International Symposium on Natural
 * Language Processing.
 * 
 * @author Matthias Ring, Stefan Herpich
 * 
 */
public class BranchAndBound extends ASSearch implements OptionHandler {

	private static final long serialVersionUID = 1L;

	private long executionTime;
	private double bound;
	private int[] primes;
	private int subsetSize;
	private int numAttributes;
	private List<Integer> ofs;
	private List<Long> partialPaths;
	private SubsetEvaluator evaluator;
	private ASEvaluation asEvaluator;
	private int[] pointer;
	private int[][] successor;
	private List<Feature> avail;
	private double delta;
	private double gamma;
	private double[][] computations;
	private String[][] types;
	private String searchType;
	private String criterionFunctionMethod;

	/**
	 * WEKA method, not implemented.
	 */
	@Override
	@SuppressWarnings("rawtypes")
	public Enumeration listOptions() {
		return null;
	}

	/**
	 * Returns a string describing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "A branch & bound feature selection. Note that this is not a WEKA class, it is implemented according to\n\n"
				+ "Matthias Ring and Bjoern M. Eskofier, Optimal Feature Selection in Nonlinear Data Using Branch-and-Bound in Kernel Space, Pattern Recognition Letters, vol. 68, pp. 56-62, 2015.";
	}

	/**
	 * WEKA method, not implemented.
	 */
	@Override
	public String[] getOptions() {
		return null;
	}

	/**
	 * Parses the WEKA options string.
	 */
	@Override
	public void setOptions(String[] options) throws Exception {
		String sizeString = null;
		String compType = null;
		String method = null;

		sizeString = Utils.getOption('S', options);
		if (!"".equals(sizeString)) {
			subsetSize = Integer.parseInt(sizeString);
		}
		compType = Utils.getOption("T", options);
		if (!"".equals(compType)) {
			searchType = compType;
		}
		method = Utils.getOption("J", options);
		if (!"".equals(method)) {
			criterionFunctionMethod = method;
		}
	}

	/**
	 * Branch and Bound initialisation
	 */
	@Override
	public int[] search(ASEvaluation ASEvaluator, Instances instances) throws Exception {
		long timeStart;
		long timeEnd;

		numAttributes = instances.numAttributes() - 1;
		evaluator = (SubsetEvaluator) ASEvaluator;
		asEvaluator = ASEvaluator;
		ofs = new LinkedList<Integer>();
		primes = MathUtilities.computePrimes(numAttributes);
		partialPaths = new LinkedList<Long>();
		bound = Double.NEGATIVE_INFINITY;
		avail = new LinkedList<Feature>();

		if (numAttributes == subsetSize) {
			return CommonUtilities.bitsetToIntegerArray(listToBitSet(ofs), false);
		}

		timeStart = System.currentTimeMillis();

		if (searchType.equals("1")) {
			chenSearch();
		} else if (searchType.equals("2")) {
			NarendraFukunagaSearch();
		} else if (searchType.equals("3")) {
			SomolPudilKittlerSearch();
		} else if (searchType.equals("4")) {
			nakariyakulCasasentSearch(ASEvaluator, instances);
		}

		timeEnd = System.currentTimeMillis();
		executionTime = timeEnd - timeStart;
		return CommonUtilities.bitsetToIntegerArray(listToBitSet(ofs), false);
	}


	/**
	 * Implementation as described in Songyot Nakariyakul, David P. Casasent,
	 * Adaptive branch and bound algorithm for selection optimal features,
	 * Pattern Recognition Letters 28 (2007).
	 */
	private void nakariyakulCasasentSearch(ASEvaluation eva, Instances data) throws Exception {
		avail = new LinkedList<Feature>();
		// step 0: Initialization
		List<Feature> order = new LinkedList<Feature>();
		List<Feature> parent = new LinkedList<Feature>();
		List<Integer> jumpLevels = new LinkedList<Integer>();

		for (int j = 1; j <= numAttributes; j++) {
			Feature tmp = new Feature(j);
			order.add(tmp);
		}
		
		AttributeSelection attsel = new AttributeSelection();   	
	    GreedyStepwise search = new GreedyStepwise();
	    search.setGenerateRanking(true);
	    search.setNumToSelect(subsetSize);
	    search.setSearchBackwards(false);
	    attsel.setEvaluator(asEvaluator);
	    attsel.setSearch(search);
	    attsel.SelectAttributes(data);
	 
	    int[] indices = attsel.selectedAttributes();
	    Arrays.sort(indices);

		
		List<Feature> opt = new LinkedList<Feature>();
		for (int i = 0; i < order.size(); i++) {
			boolean found = false;
			for(int j=0; j<indices.length-1;j++){
				if(order.get(i).getFeat() == indices[j]+1){
					found = true;
					break;
				}
			}
			if(!found){
				opt.add(order.get(i));
			}

		}
		List<Integer> sftr = new LinkedList<Integer>();
		for (int i = 0; i < opt.size(); i++) {
			sftr.add(opt.get(i).getFeat());
		}

		// setting init bound according to the in SFFS found subset
		bound = evaluator.evaluateSubset(listToBitSet(sftr));
		if (Double.isNaN(bound) || Double.isInfinite(bound)) {
			throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
		}
		ofs = sftr;

		// ordering features in avail
		sftr = new LinkedList<Integer>();
		for (int i = 0; i < numAttributes; i++) {
			int pos = 0;
			double min = Double.POSITIVE_INFINITY;

			for (int j = 0; j < order.size(); j++) {
				sftr = new LinkedList<Integer>();
				sftr.add(order.get(j).getFeat());
				if (j == 0) {
					pos = 0;
					min = evaluator.evaluateSubset(listToBitSet(sftr));
					if (Double.isNaN(min) || Double.isInfinite(min)) {
						throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
					}
				} else {
					double comp = evaluator.evaluateSubset(listToBitSet(sftr));
					if (Double.isNaN(comp) || Double.isInfinite(comp)) {
						throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
					}
					if (comp < min) {
						min = comp;
						pos = j;
					}
				}
			}
			avail.add(order.remove(pos));
			avail.get(avail.size() - 1).setPos(i + 1);
		}

		// start level k
		int k = 0;
		sftr = new LinkedList<Integer>();

		for (int i = 0; i < avail.size(); i++) {
			sftr.add(avail.get(i).getFeat());
			k++;
			double currentBound = evaluator.evaluateSubset(listToBitSet(sftr));
			if (currentBound < bound) {
				break;
			}
		}

		// next jump level
		int dest = k;
		// parent
		parent.add(new Feature(0));
		parent.get(0).setPos(0);
		jumpLevels.add(0);

		processLevelNaCa(0, dest, jumpLevels, parent);
	}


	/**
	 * Implementation as described in Petr Somol, Pavel Pudil, Josef Kittler,
	 * Fast Branch & Bound Algorithms for Optimal Feature Selection, IEEE
	 * Transactions on pattern analysis and machine intelligence, Vol. 26, No.
	 * 7, July 2004 Also includes the algorithm of Narendra and Fukunaga from
	 * below.
	 */
	private void SomolPudilKittlerSearch() throws Exception {
		// Initialization
		// adapt these two variables to the situation
		delta = 5.0;
		gamma = 1.1;

		avail = new LinkedList<Feature>();
		List<Feature> currentPath = new LinkedList<Feature>();
		successor = new int[numAttributes - subsetSize + 1][subsetSize + 2];
		pointer = new int[numAttributes - subsetSize + 1];
		computations = new double[numAttributes - subsetSize + 1][subsetSize + 2];
		types = new String[numAttributes - subsetSize + 1][subsetSize + 2];

		// step 0: Initialization
		for (int j = 1; j <= numAttributes; j++) {
			Feature n = new Feature(j);
			avail.add(n);
			n.setA(0);
			n.setC(0);
		}

		setSuccessor(0, 1, subsetSize + 1);
		pointer[0] = 1;
		currentPath.add(new Feature(0));
		computations[0][1] = evaluator.evaluateSubset(listToBitSet(new LinkedList<Integer>()));
		types[0][1] = "C";
		processLevelSoPuKi(1, currentPath);

	}

	/**
	 * Implementation as described in Patrenahalli M. Narendra/Keinosuke
	 * Fukunaga, A Branch and Bound Algorithm for Feature Subset Selection, IEEE
	 * Transactions on Computers, Vol. C-26, No. 9, Sept. 1977. Also includes
	 * the single-branch-check and the partial path check out of the
	 * Chen-algorithm.
	 */
	private void NarendraFukunagaSearch() throws Exception {
		List<Feature> currentPath = new LinkedList<Feature>();
		successor = new int[numAttributes - subsetSize + 1][subsetSize + 2];
		pointer = new int[numAttributes - subsetSize + 1];

		// step 0: Initialization

		for (int j = 1; j <= numAttributes; j++) {
			avail.add(new Feature(j));
		}

		setSuccessor(0, 1, subsetSize + 1);
		pointer[0] = 1;
		currentPath.add(new Feature(0));

		processLevelNaFuk(1, currentPath);
	}

	/**
	 * Implementation as described in Xue-wen Chen, An improved branch and bound
	 * algorithm for feature selection, Pattern Recognition Letters 24 (2003),
	 * pp. 1925 - 1933.
	 */
	private void chenSearch() throws Exception {
		List<Feature> currentPath = new LinkedList<Feature>();
		currentPath.add(new Feature(0));
		for (int i = 0; i < numAttributes + 1; i++) {
			avail.add(new Feature(i));
		}
		processLevelChen(1, currentPath);
	}

	/**************************************************************
	 * Functions for the Chen-Algorithm
	 * 
	 **************************************************************/

	/**
	 * Evaluates all nodes in the given level of the tree according to the
	 * Chen-Algorithm.
	 * 
	 * @param level
	 * @param currentPath
	 * @throws Exception
	 */
	private void processLevelChen(int level, List<Feature> currentPath) throws Exception {
		double merit = 0;
		List<Integer> sftr = null;
		List<Feature> list = new LinkedList<Feature>();

		for (int j = currentPath.get(level - 1).getFeat() + 1; j <= subsetSize + level; j++) {
			list.add(avail.get(j));
		}

		while (!list.isEmpty()) {
			currentPath.add(list.remove(list.size() - 1));
			sftr = expandCurrentPathFeature(level, currentPath);
			if (!isAlreadyEvaluated(sftr)) {
				merit = evaluator.evaluateSubset(listToBitSet(sftr));
				if (Double.isNaN(merit) || Double.isInfinite(merit)) {
					throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
				}
				//System.out.println("merit: " + merit + " optimal bound: " + bound + " path: " + sftr);
				if (merit < bound && !isFullPath(sftr)) {
					addPartialPath(sftr);
				} else if (merit > bound) {
					if (isFullPath(sftr)) {
						bound = merit;
						ofs = sftr;
					} else {
						processLevelChen(level + 1, currentPath);
					}
				}
			}
			currentPath.remove(currentPath.size() - 1);
		}
	}

	/**************************************************************
	 * Functions for the Narendra/Fukunaga-Algorithm
	 * 
	 **************************************************************/

	/**
	 * Evaluates all nodes in the given level of the tree according to the
	 * Narendra and Fukunaga-Algorithm (mixed with Chen).
	 * 
	 * @param level
	 * @param currentPath
	 * @throws Exception
	 */
	private void processLevelNaFuk(int level, List<Feature> currentPath) throws Exception {
		double merit = 0;

		List<Feature> list = new LinkedList<Feature>();
		List<Integer> sftr = new LinkedList<Integer>();

		// step 1 - Initialize list for level
		int node = pointer[level - 1];

		computeAndSort(currentPath, level);

		sftr = new LinkedList<Integer>();

		// smallest p
		int p = getSuccessor(level - 1, node);
		// store p features in list and remove those in avail
		for (int f = 0; f < p; f++) {
			list.add(avail.remove(0));
		}
		// set successors
		for (int j = 1; j <= p; j++) {
			setSuccessor(level, j, p - j + 1);
		}

		// step 2 - select new node
		while (!list.isEmpty()) {
			Feature z = list.get(list.size() - 1);
			pointer[level] = list.size();
			list.remove(list.size() - 1);

			// step 3
			currentPath.add(z);
			sftr = expandCurrentPathFeature(level, currentPath);
			// check single branch
			if (p == 1 && (numAttributes - subsetSize) != level) {
				processLevelNaFuk(level + 1, currentPath);
			} else if (!isAlreadyEvaluated(sftr)) {
				merit = evaluator.evaluateSubset(listToBitSet(sftr));
				if (Double.isNaN(merit) || Double.isInfinite(merit)) {
					throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
				}
				if (merit < bound) {
					if (!isFullPath(sftr))
						addPartialPath(sftr);
					avail.add(currentPath.remove(currentPath.size() - 1));
					// goto step 4
				} else if (sftr.size() == (numAttributes - subsetSize)) {
					// step 5
					avail.add(currentPath.remove(currentPath.size() - 1));
					ofs = sftr;
					bound = merit;
				} else {
					processLevelNaFuk(level + 1, currentPath);
				}
			} else {
				avail.add(currentPath.remove(currentPath.size() - 1));
			}
		}

		// step 4: Backtrack
		if (--level == 0)
			return;
		avail.add(currentPath.remove(currentPath.size() - 1));
	}

	/**
	 * Computes the criterion function for each possible path in a level and
	 * accordingly sorts avail.
	 * 
	 * @param path
	 * @param level
	 */
	private void computeAndSort(List<Feature> path, int level) throws Exception {
		List<Integer> sftr = new LinkedList<Integer>();
		double[] merits = new double[avail.size()];

		// compute criterion function for all k in avail
		for (int k = 0; k < avail.size(); k++) {
			path.add(avail.get(k));
			sftr = expandCurrentPathFeature(level, path);
			// merits[k] = evaluator.evaluateSubset(listToBitSet(sftr));
			avail.get(k).setJ(evaluator.evaluateSubset(listToBitSet(sftr)));
			if (Double.isNaN(merits[k]) || Double.isInfinite(merits[k])) {
				throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
			}
			path.remove(path.size() - 1);
		}

		// sort avail in increasing order of the computed results
		bubblesort(avail);
		// double[] copy = new double[merits.length];
		// for (int c = 0; c < copy.length; c++) {
		// copy[c] = merits[c];
		// }
		// Arrays.sort(merits);
		// List<Feature> order = new LinkedList<Feature>();
		// for (int d = 0; d < copy.length; d++) {
		// for (int e = 0; e < copy.length; e++) {
		// if (merits[d] == copy[e]) {
		// order.add(avail.get(e));
		// }
		// }
		// }
		// avail = order;
	}

	/**
	 * Getter/Setter for the successors in the different search tree leves.
	 */
	private int getSuccessor(int level, int position) {
		return successor[level][position];
	}

	private void setSuccessor(int level, int position, int value) {
		successor[level][position] = value;
	}

	/**************************************************************
	 * Functions for the Somol/Pudil/Kittler algorithm
	 * 
	 **************************************************************/

	/**
	 * Evaluates all nodes in the given level of the tree according to the
	 * Somol/Pudil/Kittler-Algorithm (mixed with Chen & Narendra/Fukunaga).
	 * 
	 * @param level
	 * @param currentPath
	 * @throws Exception
	 */
	private void processLevelSoPuKi(int level, List<Feature> currentPath) throws Exception {

		List<Feature> list = new LinkedList<Feature>();
		List<Integer> sftr = new LinkedList<Integer>();
		double[] sortVector = new double[avail.size()];

		// step 1 - Initialize list for level
		int node = pointer[level - 1];
		for (int j = 0; j < avail.size(); j++) {
			if (avail.get(j).getC() > delta && (numAttributes - subsetSize) > level) {
				// predict
				sortVector[j] = computations[level - 1][pointer[level - 1]] - avail.get(j).getA();
				avail.get(j).setT("P");
			} else {
				// compute
				currentPath.add(avail.get(j));
				sftr = expandCurrentPathFeature(level, currentPath);
				sortVector[j] = evaluator.evaluateSubset(listToBitSet(sftr));
				if (Double.isNaN(sortVector[j]) || Double.isInfinite(sortVector[j])) {
					throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
				}
				avail.get(j).setT("C");
				currentPath.remove(currentPath.size() - 1);
			}
		}
		// sort
		// double[] copy = new double[sortVector.length];
		// for (int c = 0; c < copy.length; c++) {
		// copy[c] = sortVector[c];
		// }
		// Arrays.sort(sortVector);
		//
		// // ascending order
		// List<Feature> order = new LinkedList<Feature>();
		// for (int d = 0; d < copy.length; d++) {
		// for (int e = 0; e < copy.length; e++) {
		// if (sortVector[d] == copy[e]) {
		// order.add(avail.get(e));
		// }
		// }
		// }
		// avail = order;
		bubblesort(avail);

		sftr = new LinkedList<Integer>();

		// successors p
		int p = getSuccessor(level - 1, node);

		// store p features in list and remove those in avail
		for (int f = 0; f < p; f++) {
			Feature fea = avail.remove(0);
			types[level][f + 1] = fea.getT();
			if (types[level][f + 1].equals("C")) {
				computations[level][f + 1] = sortVector[f];
			} else {
				computations[level][f + 1] = computations[level - 1][pointer[level - 1]] - gamma * fea.getA();
			}
			list.add(fea);
		}

		// set successors
		for (int j = 1; j <= p; j++) {
			setSuccessor(level, j, p - j + 1);
		}

		// step 2 - select new node
		while (!list.isEmpty()) {

			Feature z = list.get(list.size() - 1);
			pointer[level] = list.size();
			list.remove(list.size() - 1);
			currentPath.add(z);
			sftr = expandCurrentPathFeature(level, currentPath);

			if (p == 1 && (numAttributes - subsetSize) != level) {
				processLevelSoPuKi(level + 1, currentPath);
			} else if (!isAlreadyEvaluated(sftr)) {
				if (types[level][pointer[level]].equals("P") && computations[level][pointer[level]] < bound) {
					double merit = evaluator.evaluateSubset(listToBitSet(sftr));
					if (Double.isNaN(merit) || Double.isInfinite(merit)) {
						throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
					}
					if (types[level - 1][pointer[level - 1]].equals("C")) {
						double val = (z.getA() * z.getC() + computations[level - 1][pointer[level - 1]] - merit) / (z.getC() + 1);
						z.setA(val);
						z.setC(z.getC() + 1);
					}
					computations[level][pointer[level]] = merit;
					types[level][pointer[level]] = "C";

				}
				if (types[level][pointer[level]].equals("C") && computations[level][pointer[level]] < bound) {
					if (!isFullPath(sftr))
						addPartialPath(sftr);
					avail.add(currentPath.remove(currentPath.size() - 1));
				} else if (sftr.size() == (numAttributes - subsetSize)) {
					// step 5
					if (computations[level][pointer[level]] > bound) {
						ofs = sftr;
						bound = computations[level][pointer[level]];
					}
					avail.add(currentPath.remove(currentPath.size() - 1));
				} else {
					processLevelSoPuKi(level + 1, currentPath);
				}
			} else {
				avail.add(currentPath.remove(currentPath.size() - 1));
			}
		}

		// step 4: Backtrack
		if (--level == 0)
			return;
		avail.add(currentPath.remove(currentPath.size() - 1));
	}

	/**************************************************************
	 * Nakariyakul/Casasent functions
	 * 
	 **************************************************************/


	/**
	 * Generates a list with all possible paths from the parent node to the jump
	 * level.
	 * 
	 * @param level
	 * @param stop
	 * @param parent
	 * @param list
	 * @return
	 */
	private List<List<Feature>> generateList(int level, int stop, List<Feature> parent, List<List<Feature>> list) {
		if (level == stop) {
			List<Feature> tmp = new LinkedList<Feature>();
			for (int i = 0; i < parent.size(); i++) {
				tmp.add(parent.get(i));
			}
			list.add(tmp);
			parent.remove(parent.size() - 1);
		} else {
			for (int j = parent.get(level - 1).getPos() + 1; j <= subsetSize + level; j++) {
				List<Feature> path = parent;
				for (int i = 0; i < avail.size(); i++) {
					if (avail.get(i).getPos() == j) {
						path.add(avail.get(i));
						generateList(level + 1, stop, parent, list);
					}
				}
			}
			parent.remove(parent.size() - 1);
		}

		return list;
	}

	/**
	 * Evaluates all nodes in the given level of the tree according to the
	 * Nakariyakul/Casasent-Algorithm. Includes alternative criterion function
	 * models as described in Songyot Nakariyakul, Study on Criterion Function
	 * Models in Adaptive Branch and Bound Algorithm, 2009 Eighth International
	 * Symposium on Natural Language Processing.
	 * 
	 * @param level
	 * @param dest
	 * @param jumpLevels
	 * @param parent
	 * @throws Exception
	 */
	private void processLevelNaCa(int level, int dest, List<Integer> jumpLevels, List<Feature> parent) throws Exception {

		List<List<Feature>> list = new LinkedList<List<Feature>>();
		// step 1
		list = generateList(parent.size(), dest + 1, parent, list);

		jumpLevels.add(dest);

		List<Integer> sftr;

		// step 2 and 3
		// right-left strategy with partial paths
		// single-branching nodes
		for (int i = list.size() - 1; i >= 0; i--) {
			sftr = new LinkedList<Integer>();
			List<Feature> tmp = list.get(i);

			sftr = expandCurrentPathFeature(tmp.size() - 1, tmp);

			if (isAlreadyEvaluated(sftr)) {
				list.remove(i);
				continue;
			}

			if (tmp.get(tmp.size() - 1).getPos() == dest + subsetSize) {

				int a = 0;
				while (sftr.size() != (numAttributes - subsetSize)) {
					sftr.add(avail.get(dest + subsetSize + a).getFeat());
					a++;
				}

				double merit = evaluator.evaluateSubset(listToBitSet(sftr));
				if (Double.isNaN(merit) || Double.isInfinite(merit)) {
					throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
				}
				list.remove(i);
				if (merit > bound) {
					bound = merit;
					ofs = sftr;
				}
			}
		}

		// step 4
		while (list.size() != 0) {

			int pos = 0;
			double max = Double.NEGATIVE_INFINITY;

			for (int i = 0; i < list.size(); i++) {
				sftr = new LinkedList<Integer>();
				sftr = expandCurrentPathFeature(list.get(i).size() - 1, list.get(i));
				if (i == 0) {
					max = evaluator.evaluateSubset(listToBitSet(sftr));
					if (Double.isNaN(max) || Double.isInfinite(max)) {
						throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
					}
				} else {
					double temp = evaluator.evaluateSubset(listToBitSet(sftr));
					if (Double.isNaN(temp) || Double.isInfinite(temp)) {
						throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
					}
					if (temp > max) {
						max = temp;
						pos = i;
					}
				}
			}

			parent = list.remove(pos);

			sftr = new LinkedList<Integer>();
			double valueAllFeat = 0;
			double beta = 0;
			int maxK = 0;
			for (int i = 0; i < jumpLevels.size(); i++) {
				if (jumpLevels.get(i) > maxK) {
					maxK = jumpLevels.get(i);
				}
			}

			if (criterionFunctionMethod.equals("1")) {
				valueAllFeat = evaluator.evaluateSubset(listToBitSet(sftr));
				if (Double.isNaN(valueAllFeat) || Double.isInfinite(valueAllFeat)) {
					throw new Exception("Bhattacharyya distance cannot be computed (very possibly: one of the covariance matrices is singular)");
				}
				beta = Math.log(1 - max / valueAllFeat) / Math.log(maxK / numAttributes);
			} else {
				valueAllFeat = max;
			}

			// step 5
			if (max < bound) {
				for (int i = 0; i < list.size(); i++) {
					sftr = new LinkedList<Integer>();
					sftr = expandCurrentPathFeature(list.get(i).size() - 1, list.get(i));
					addPartialPath(sftr);
				}
				list.clear();
			} else {
				sftr = new LinkedList<Integer>();
				sftr = expandCurrentPathFeature(parent.size() - 1, parent);
				if (max > bound && maxK == (numAttributes - subsetSize)) {
					bound = max;
					ofs = sftr;
					list.clear();
				} else {
					if (criterionFunctionMethod.equals("1")) {
						dest = (int) (Math.ceil(Math.pow(numAttributes * (1 - bound / valueAllFeat), (1 / beta))));
					} else if (criterionFunctionMethod.equals("2")) {
						dest = (int) (Math.ceil(((-bound) * (numAttributes - parent.size() - 1) / valueAllFeat) + numAttributes));
					} else if (criterionFunctionMethod.equals("3")) {
						dest = (int) (Math.ceil(Math.log((-bound * (Math.E - 1) / valueAllFeat) + Math.E) * (numAttributes - parent.size() - 1) + parent.size()
								- 1));
					} else if (criterionFunctionMethod.equals("4")) {
						dest = (int) (Math.ceil(Math.sqrt((valueAllFeat - bound) / valueAllFeat) * (numAttributes - parent.size() - 1) + parent.size() - 1));
					}
					if (dest > numAttributes - subsetSize) {
						dest = numAttributes - subsetSize;
					}
					processLevelNaCa(parent.size() - 1, dest, jumpLevels, parent);
				}
			}
		}

		int maxK = 0;
		for (int i = 0; i < jumpLevels.size(); i++) {
			if (jumpLevels.get(i) > maxK) {
				maxK = jumpLevels.get(i);
			}
		}
		for (int i = 0; i < jumpLevels.size(); i++) {
			if (jumpLevels.get(i) == maxK) {
				jumpLevels.remove(i);
			}
		}
	}

	
	/**************************************************************
	 * Common Functions
	 * 
	 **************************************************************/

	public List<Feature> bubblesort(List<Feature> sortArray) {
		Feature temp;
		for (int i = 1; i < sortArray.size(); i++) {
			for (int j = 0; j < sortArray.size() - i; j++) {
				if (sortArray.get(j).getJ() > sortArray.get(j + 1).getJ()) {
					temp = sortArray.get(j);
					sortArray.set(j, sortArray.get(j + 1));
					sortArray.set(j + 1, temp);
				}
			}
		}
		return sortArray;
	}

	/**
	 * Creates a path that describes the current position.
	 * 
	 * @param level
	 * @param currentPath
	 * @return
	 */
	private List<Integer> expandCurrentPathFeature(int level, List<Feature> currentPath) throws Exception {
		List<Integer> sftr = new LinkedList<Integer>();

		for (int j = 1; j <= level; j++) {
			sftr.add(currentPath.get(j).getFeat());
		}

		if (searchType.equals("1")) {
			if (currentPath.get(level).getFeat() == level + subsetSize) {
				for (int j = currentPath.get(level).getFeat() + 1; j <= numAttributes; j++) {
					sftr.add(j);
				}
			}
		}

		return sftr;
	}

	/**
	 * Converts the integer list to a bitset object.
	 * 
	 * @param sftr
	 * @return
	 */
	private BitSet listToBitSet(List<Integer> sftr) {
		BitSet bitSet = new BitSet(numAttributes + 1);
		bitSet.set(0, numAttributes);

		for (Integer featureNumber : sftr) {
			bitSet.clear(featureNumber - 1);
		}

		return bitSet;
	}

	/**
	 * Internal method to decide if the path evaluation can be skipped.
	 * 
	 * @param sftr
	 * @return
	 */
	private long computePathProduct(List<Integer> sftr) {
		long pathProduct = 1;

		for (Integer featureNumber : sftr) {
			pathProduct *= (long) primes[featureNumber - 1];
		}
		return pathProduct;
	}

	/**
	 * Internal method to decide if the path evaluation can be skipped.
	 * 
	 * @param sftr
	 * @return
	 */
	private boolean isAlreadyEvaluated(List<Integer> sftr) {
		long pathProduct = computePathProduct(sftr);

		for (Long partialPath : partialPaths) {
			if (pathProduct % partialPath == 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Decides if the path ends at a leaf.
	 * 
	 * @param sftr
	 * @return
	 */
	private boolean isFullPath(List<Integer> sftr) {
		return sftr.size() == (numAttributes - subsetSize);
	}

	/**
	 * Adds the given path to the list of all evaluated paths.
	 * 
	 * @param sftr
	 */
	private void addPartialPath(List<Integer> sftr) {
		if (!isFullPath(sftr)) {
			partialPaths.add(computePathProduct(sftr));
		}
	}

	public double getFinalBound() {
		return bound;
	}

	public long getExecutionTime() {
		return executionTime;
	}

}
