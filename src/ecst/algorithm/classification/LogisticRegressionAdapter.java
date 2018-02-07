package ecst.algorithm.classification;

import ecst.algorithm.ClassificationAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import weka.classifiers.Classifier;
import weka.classifiers.functions.Logistic;

public class LogisticRegressionAdapter extends ClassificationAlgorithm {

	private Parameter iterations;
	private Parameter ridge;

	@Override
	protected void initClassifierParameters() {
		iterations = new Parameter(-1, "Number of iterations", Parameter.TYPE.INTEGER, "-M", "classifier.maxIts");
		ridge = new Parameter(1.0e-8, "Ridge in log-likelihood", Parameter.TYPE.DOUBLE, "-R", "classifier.ridge");
	}

	@Override
	public Parameter[] getParameters() {
		return new Parameter[] { iterations, ridge };
	}

	@Override
	public Class<? extends Object> getImplementingClass() {
		return Logistic.class;
	}

	@Override
	protected Parameter[] getGridSearchParameters() {
		return new Parameter[] { iterations, ridge };
	}

	@Override
	protected void postprocess(Classifier classifier, PipelineData data) throws Exception {
	}

	@Override
	protected void analyzeSystem(PipelineData data, Classifier classifier) throws Exception {
	}

	@Override
	protected void addDependencies() {
	}

	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}

	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}

}
