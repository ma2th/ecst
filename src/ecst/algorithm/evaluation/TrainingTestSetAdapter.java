package ecst.algorithm.evaluation;

import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;
import ecst.algorithm.EvaluationAlgorithm;
import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.InstanceUtilities;

/**
 * This class implements the training-test-set evaluation method.
 * 
 * @author Matthias Ring
 *
 */
public class TrainingTestSetAdapter extends EvaluationAlgorithm {
	
	private Parameter testFile;

	/**
	 * Returns the name of the implementing class (= this)
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return TrainingTestSetAdapter.class;
	}
	
	/**
	 * Returns a string describing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Evaluates the classifier with the given test file. For more information, see\n\n" +
				"S. Theodoridis and K. Koutroumbas, Pattern recognition, Academic Press, 2006.";
	}
	
	/**
	 * Initializes all paramters.
	 */
	@Override
	public void initParameters() {
		testFile = new Parameter("", "Test set file name", Parameter.TYPE.FILE_NAME, null);
	}

	/**
	 * Returns all paramters.
	 */
	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { testFile };
	}

	/**
	 * Evaluates the given classifier with the given pipeline data. Performs an
	 * inner feature selection if the argument is not null.
	 */
	@Override
	public Evaluation evaluate(PipelineData data, Classifier classifier, FeatureSelectionAlgorithm featureSelection) throws Exception {
		DataSource source = null;
		Evaluation evaluation = null;
		Instances testInstances = null;

		if (testFile.getValue() == null || testFile.getValue().equals("")) {
			throw new IllegalArgumentException("Please specify a test file");
		}
		source = new DataSource((String) testFile.getValue());
		testInstances = source.getDataSet();
		evaluation = new Evaluation(data.getFeatureSelectedInstances());
		
		InstanceUtilities.evaluateWithTestInstances(data.getFeatureSelectedInstances(), testInstances, classifier, evaluation);
		
		return evaluation;
	}

}
