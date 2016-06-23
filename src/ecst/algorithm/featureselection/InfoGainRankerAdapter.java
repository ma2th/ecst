package ecst.algorithm.featureselection;

import ecst.algorithm.FeatureSelectionAlgorithm;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.InfoGainAttributeEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;

/**
 *
 * 
 * @author Matthias Ring
 *
 */
public class InfoGainRankerAdapter extends FeatureSelectionAlgorithm {
	
	private Parameter binarize;
	private Parameter missingMerge;
	
	private Parameter startSet;
	private Parameter threshold;
	private Parameter number;
	

	@Override
	protected void initSearchMethodParameters() {
		startSet = new Parameter("", "Start set", Parameter.TYPE.STRING, "-P");
		threshold = new Parameter(-1.7976931348623157E308, "Threshold for discarding attributes", Parameter.TYPE.DOUBLE, "-T");
		number = new Parameter(-1, "Number of attributes to select", Parameter.TYPE.INTEGER, "-N");
	}

	@Override
	protected Parameter[] getSearchMethodParameters() {
		return new Parameter[] { startSet, threshold, number };
	}

	@Override
	protected void initEvaluatorParameters() {
		binarize = new Parameter(false, "Binarize numeric attributes", Parameter.TYPE.BOOLEAN, "-B");
		missingMerge = new Parameter(true, "Distribute counts for missing values", Parameter.TYPE.BOOLEAN, "-M");
	}

	@Override
	protected Parameter[] getEvaluatorParameters() {
		return new Parameter[] { binarize, missingMerge };
	}

	@Override
	public ASEvaluation createEvaluator(Classifier classifier, PipelineData data) {
		InfoGainAttributeEval evaluator = new InfoGainAttributeEval();
		
		evaluator.setMissingMerge((Boolean) missingMerge.getValue());
		evaluator.setBinarizeNumericAttributes((Boolean) binarize.getValue());
		
		return evaluator;
	}

	@Override
	protected String getAdditionalInformationAboutSearchProcess(ASSearch search, ASEvaluation evaluator) {
		return null;
	}

	@Override
	public Class<? extends Object> getImplementingClass() {
		return Ranker.class;
	}

}
