package ecst.algorithm.analysis;

import java.util.LinkedList;
import java.util.List;

import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.MultiplierConstants;
import ecst.algorithm.definition.OperationDefinition;
import ecst.algorithm.definition.StaticSpace;
import ecst.combiner.CombinerOutputModel;
import ecst.utilities.CommonUtilities;
import ecst.utilities.MathUtilities;

/**
 * This class returns different measures of the complexity analysis.
 * 
 * @author Matthias Ring
 * 
 */
public class Analysis {

	private CombinerOutputModel model;

	/**
	 * Constructor.
	 * 
	 * @param model
	 *            the trained pipeline that should be analyzed.
	 */
	public Analysis(CombinerOutputModel model) {
		this.model = model;
	}

	/**
	 * Returns the total number of operations necessary for one classification
	 * decision.
	 * 
	 * @return
	 */
	public Integer getTotalOperations() {
		int sum = 0;
		int[] current;
		List<OperationDefinition> operations = AlgorithmBox.getInstance().getOperationDefinitions();

		for (int i = 0; i < operations.size(); i++) {
			current = getOperations(operations.get(i));
			for (int j = 0; j < current.length; j++) {
				sum += current[j];
			}
		}

		return sum;
	}

	/**
	 * Returns the number of a specific operation.
	 * 
	 * @param definition
	 * @return
	 */
	public int[] getOperations(OperationDefinition definition) {
		int[] sum = new int[3];

		for (int i = 0; i < sum.length; i++) {
			sum[i] = 0;
		}

		if (model.getFeatureExtractionOperations() != null) {
			sum[0] = sumOperations(model.getFeatureExtractionOperations(), definition);
		}
		if (model.getPreprocessingOperations() != null) {
			sum[1] = sumOperations(model.getPreprocessingOperations(), definition);
		}
		sum[2] = sumOperations(model.getClassificationOperations(), definition);

		return sum;
	}

	/**
	 * Returns the total space requirements.
	 * 
	 * @return
	 */
	public int getTotalSpace() {
		return MathUtilities.sumIntArray(getNumberOfFloats()) + MathUtilities.sumIntArray(getNumberOfIntegers());
	}

	/**
	 * Returns the number of integer parameters.
	 * 
	 * @return
	 */
	public int[] getNumberOfIntegers() {
		int[] sum = new int[3];

		for (int i = 0; i < sum.length; i++) {
			sum[i] = 0;
		}

		if (model.getFeatureExtractionSpace() != null) {
			sum[0] = sumSpace(filterList(model.getFeatureExtractionSpace(), StaticSpace.INTEGER_TYPE));
		}
		if (model.getPreprocessingAlgorithm() != null) {
			sum[1] = sumSpace(filterList(model.getPreprocessingSpace(), StaticSpace.INTEGER_TYPE));
		}
		sum[2] = sumSpace(filterList(model.getClassificationSpace(), StaticSpace.INTEGER_TYPE));

		return sum;
	}

	/**
	 * Returns the number of float parameters.
	 * 
	 * @return
	 */
	public int[] getNumberOfFloats() {
		int[] sum = new int[3];

		for (int i = 0; i < sum.length; i++) {
			sum[i] = 0;
		}

		if (model.getFeatureExtractionSpace() != null) {
			sum[0] = sumSpace(filterList(model.getFeatureExtractionSpace(), StaticSpace.DOUBLE_TYPE));
		}
		if (model.getPreprocessingAlgorithm() != null) {
			sum[1] = sumSpace(filterList(model.getPreprocessingSpace(), StaticSpace.DOUBLE_TYPE));
		}
		sum[2] = sumSpace(filterList(model.getClassificationSpace(), StaticSpace.DOUBLE_TYPE));

		return sum;
	}
	 

	/**
	 * Internation method to compute the number of parameters.
	 * 
	 * @param list
	 * @param filter
	 * @return
	 */
	private List<DynamicSpace> filterList(List<DynamicSpace> list, String filter) {
		List<DynamicSpace> result = new LinkedList<DynamicSpace>();

		if (list != null) {
			for (DynamicSpace space : list) {
				if (filter.equals(space.getStaticSpace().getType())) {
					result.add(space);
				}
			}
		}

		return result;
	}

	/**
	 * Sums the numbers in the given list up.
	 * 
	 * @param list
	 * @return
	 */
	private int sumSpace(List<DynamicSpace> list) {
		int sum = 0;
		int number = 1;

		for (DynamicSpace dynamicSpace : list) {
			if (dynamicSpace.getDependsOnFeatures() == null
					|| CommonUtilities.listContainsAnyElement(model.getData().getAttributesAfterTraining(), dynamicSpace.getDependsOnFeatures())) {
				number = 1;
				for (DynamicMultiplier multiplier : dynamicSpace.getMultiplierList()) {
					if (multiplier.getFactor() == null) {
						multiplier.setFactor(calculateFactor(multiplier.getName()));
					}
					number *= multiplier.getFactor();
				}
				sum += number;
			}
		}
		return sum;
	}

	/**
	 * Sums the numbers of the given operation definition in the given list up.
	 * 
	 * @param list
	 * @param definition
	 * @return
	 */
	private int sumOperations(List<DynamicOperations> list, OperationDefinition definition) {
		int sum = 0;
		int number = 0;

		for (DynamicOperations dynamicOperations : list) {
			if (dynamicOperations.getDependsOnFeatures() == null
					|| CommonUtilities.listContainsAnyElement(model.getData().getAttributesAfterTraining(), dynamicOperations.getDependsOnFeatures())) {
				if (dynamicOperations.getStaticOperations().getOperationDefinitions().containsKey(definition)) {
					number = dynamicOperations.getStaticOperations().getOperationDefinitions().get(definition);
					for (DynamicMultiplier multiplier : dynamicOperations.getMultiplierList()) {
						if (multiplier.getFactor() == null) {
							multiplier.setFactor(calculateFactor(multiplier.getName()));
						}
						number *= multiplier.getFactor();
					}
					sum += number;
				}
			}
		}
		return sum;
	}

	/**
	 * Evaluates the value of the standard multipliers.
	 * 
	 * @param name
	 * @return
	 */
	private Integer calculateFactor(String name) {
		if (MultiplierConstants.ATTRIBUTES.equals(name)) {
			return model.getData().getNumberOfAttributesAfterTraining();
		} else if (MultiplierConstants.ATTRIBUTES_MINUS_ONE.equals(name)) {
			return model.getData().getNumberOfAttributesAfterTraining() - 1;
		} else if (MultiplierConstants.CLASSES.equals(name)) {
			return model.getData().getNumberOfClassesAfterTraining();
		} else if (MultiplierConstants.CLASSES_MINUS_ONE.equals(name)) {
			return model.getData().getNumberOfClassesAfterTraining() - 1;
		} else if (MultiplierConstants.USER_SELECTED_ATTRIBUTES.equals(name)) {
			return model.getData().getNumberOfUserSelectedAttributesAfterTraining();
		} else if (MultiplierConstants.INSTANCES.equals(name)) {
			return model.getData().getNumberOfInstancesAfterTraining();
		} else if (MultiplierConstants.LINES_PER_FEATURE.equals(name)) {
			return model.getFeatureExtractionModel().getInputLinesForOneFeature();
		} else if (MultiplierConstants.LINES_PER_FEATURE_MINUS_ONE.equals(name)) {
			return model.getFeatureExtractionModel().getInputLinesForOneFeature() - 1;
		} else if (MultiplierConstants.GAUSS_SUM_OF_LINES_PER_FEATURE_MINUS_ONE.equals(name)) {
			return MathUtilities.gaussSum(model.getFeatureExtractionModel().getInputLinesForOneFeature() - 1);
		} else if (MultiplierConstants.CONSTANT.equals(name)) {
			return 1;
		} else {
			throw new IllegalArgumentException("Unknown multiplier name: " + name);
		}
	}
}
