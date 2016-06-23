package ecst.algorithm.evaluation;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.instance.Resample;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.combiner.PipelineData;
import ecst.utilities.InstanceUtilities;
import ecst.utilities.ParameterUtilities;

/**
 * This class implements the bootstrapping evaluation algorithm according to R.
 * Polikar, Bootstrap Inspired Techniques in Computational Intelligence, IEEE
 * Signal Processing Magazine 24(4):56-72, 2007.
 * 
 * @author Matthias Ring
 * 
 */
public class BootstrappingAdapter extends EvaluationAlgorithm {

	private Parameter bias;
	private Parameter sizeOfResamplingData;
	private Parameter numberOfResampleIterations;
	private SelectedParameterItem inputDataDistribution;
	private SelectedParameterItem uniformDistribution;

	/**
	 * Returns the name of the implementing class (= this)
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return BootstrappingAdapter.class;
	}

	/**
	 * Returns a string description of this class.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Performs a bootstrapping evaluation. Note that this is not a WEKA class, it is implemented according to\n\n"
				+ "R. Polikar, Bootstrap Inspired Techniques in Computational Intelligence, "
				+ "IEEE Signal Processing Magazine 24(4):56-72, 2007.";
	}

	/**
	 * Initializes all paramters.
	 */
	@Override
	public void initParameters() {
		inputDataDistribution = new SelectedParameterItem("Distribution in input data", null);
		uniformDistribution = new SelectedParameterItem("Uniform class distribution", null);
		bias = ParameterUtilities.createSelectedParameter("Specify a bias towards a uniform distribution", inputDataDistribution,
				uniformDistribution);
		numberOfResampleIterations = new Parameter(10, "Number of resampling iterations", Parameter.TYPE.INTEGER, null);
		sizeOfResamplingData = new Parameter(100.0, "Size of the resampled data set (percent of input set)", Parameter.TYPE.DOUBLE, null);
	}

	/**
	 * Returns all paramters.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { bias, sizeOfResamplingData, numberOfResampleIterations };
	}

	/**
	 * Evaluate the given classifier with the given pipeline data. Performs an
	 * inner feature selection if the inner algorithm is not null.
	 */
	@Override
	public Evaluation evaluate(PipelineData data, Classifier classifier, FeatureSelectionAlgorithm featureSelection) throws Exception {
		Classifier classifierCopy = null;
		Resample resampleFilter = null;
		Instances testInstances = null;
		Instances trainingInstances = null;
		Evaluation evaluation = null;
		SelectedParameter item = (SelectedParameter) bias.getValue();
		SelectedParameterItem entry = item.getItems().get(item.getSelectedIndex());
		AttributeSelectedClassifier metaClassifier = new AttributeSelectedClassifier();

		if (numberOfResampleIterations.getValue() == null || (Integer) numberOfResampleIterations.getValue() <= 0) {
			throw new IllegalArgumentException("Number of resampling iterations must be greater than zero!");
		}

		evaluation = new Evaluation(data.getPreprocessedInstances());
		for (int i = 0; i < (Integer) numberOfResampleIterations.getValue(); i++) {
			resampleFilter = new Resample();
			resampleFilter.setInputFormat(data.getPreprocessedInstances());
			if (inputDataDistribution.equals(entry)) {
				resampleFilter.setBiasToUniformClass(0);
			} else {
				resampleFilter.setBiasToUniformClass(1);
			}
			resampleFilter.setNoReplacement(false);
			resampleFilter.setRandomSeed((int) System.nanoTime());
			if (sizeOfResamplingData.getValue() != null) {
				resampleFilter.setSampleSizePercent((Double) sizeOfResamplingData.getValue());
			}

			trainingInstances = Filter.useFilter(data.getPreprocessedInstances(), resampleFilter);
			testInstances = InstanceUtilities.difference(data.getPreprocessedInstances(), trainingInstances);

			if (featureSelection != null) {
				metaClassifier.setEvaluator(featureSelection.createEvaluator(classifier, data));
				metaClassifier.setSearch(featureSelection.createSearchMethod());
				metaClassifier.setClassifier(classifier);
				metaClassifier.buildClassifier(trainingInstances);
				InstanceUtilities.evaluateWithTestInstances(trainingInstances, testInstances, metaClassifier, evaluation);
			} else {
				classifierCopy = Classifier.makeCopy(classifier);
				classifierCopy.buildClassifier(trainingInstances);
				InstanceUtilities.evaluateWithTestInstances(trainingInstances, testInstances, classifierCopy, evaluation);
			}
		}

		return evaluation;
	}

}
