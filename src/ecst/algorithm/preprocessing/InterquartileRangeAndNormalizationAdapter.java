package ecst.algorithm.preprocessing;

import java.util.LinkedList;
import java.util.List;

import weka.core.Instances;
import ecst.algorithm.PreprocessingAlgorithm;
import ecst.algorithm.analysis.DynamicMultiplier;
import ecst.algorithm.analysis.DynamicOperations;
import ecst.algorithm.analysis.DynamicSpace;
import ecst.algorithm.definition.AlgorithmBox;
import ecst.algorithm.definition.AlgorithmDefinition;
import ecst.algorithm.parameter.Parameter;
import ecst.combiner.PipelineData;
import ecst.utilities.ParameterUtilities;

/**
 * This class is an adapter for the sequential execution of InterquartileRange
 * and Normalization.
 * 
 * @author Matthias Ring
 * 
 */
public class InterquartileRangeAndNormalizationAdapter extends PreprocessingAlgorithm {

	private NormalizationAdapter normalizationAdapter;
	private InterquartileRangeAdapter interquartileRangeAdapter;

	/**
	 * Returns a string descibing this algorithm.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Executes the Interquartile Range algorithm and afterwards the Normalization algorithm.";
	}

	/**
	 * Performs InterquartileRange and Normalization.
	 */
	@Override
	protected Instances filterData(PipelineData data) throws Exception {
		Instances instances = interquartileRangeAdapter.preprocess(data);
		PipelineData newData = new PipelineData(instances);
		instances = normalizationAdapter.preprocess(newData);
		return instances;
	}

	/**
	 * Nothing to do here.
	 */
	@Override
	protected void analyzeSystem(PipelineData data) throws Exception {
	}

	/**
	 * No dependencies in this algorithm.
	 */
	@Override
	protected void addDependencies() {
	}

	/**
	 * No algorithm-specific multipliers in this algorithm.
	 */
	@Override
	protected void setMultiplier(DynamicMultiplier multiplier) {
	}

	/**
	 * Initializes the algorithm parameters.
	 */
	@Override
	public void initParameters() {
		try {
			for (AlgorithmDefinition definition : AlgorithmBox.getInstance().getPreprocessingAlgorithms()) {
				if (definition.getClassName().equals(NormalizationAdapter.class.getCanonicalName())) {
					normalizationAdapter = (NormalizationAdapter) definition.createInstance();
				}
			}
			for (AlgorithmDefinition definition : AlgorithmBox.getInstance().getPreprocessingAlgorithms()) {
				if (definition.getClassName().equals(InterquartileRangeAdapter.class.getCanonicalName())) {
					interquartileRangeAdapter = (InterquartileRangeAdapter) definition.createInstance();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the parameters of this algorithm.
	 */
	@Override
	public Parameter[] getParameters() {
		return ParameterUtilities.mergeParameters(normalizationAdapter.getParameters(), interquartileRangeAdapter.getParameters());
	}

	/**
	 * Returns the class implementing this algorithm.
	 */
	@Override
	public Class<? extends Object> getImplementingClass() {
		return InterquartileRangeAndNormalizationAdapter.class;
	}

	/**
	 * This method from WorkPhaseAlgorithm is overwritten to enable the
	 * sequential execution of two algorithms.
	 */
	@Override
	public List<DynamicSpace> getDynamicSpaceList() {
		List<DynamicSpace> list = new LinkedList<DynamicSpace>();

		list.addAll(normalizationAdapter.getDynamicSpaceList());
		list.addAll(interquartileRangeAdapter.getDynamicSpaceList());

		return list;
	}

	/**
	 * This method from WorkPhaseAlgorithm is overwritten to enable the
	 * sequential execution of two algorithms.
	 */
	@Override
	public List<DynamicOperations> getDynamicOperationsList() {
		List<DynamicOperations> list = new LinkedList<DynamicOperations>();

		list.addAll(normalizationAdapter.getDynamicOperationsList());
		list.addAll(interquartileRangeAdapter.getDynamicOperationsList());

		return list;
	}

	/**
	 * Not yet implemented.
	 */
	@Override
	public String modelToXML(PipelineData data) {
		return null;
	}
	
	@Override
	protected String getPreprocessingModel(PipelineData data) {
		return "not yet implemented";
	}

}
