package ecst.algorithm;

import weka.core.Instances;
import ecst.combiner.PipelineData;

/**
 * This is the basis class that all preprocessing algorithms have to extend.
 * 
 * @author Matthias Ring
 * 
 */
public abstract class PreprocessingAlgorithm extends WorkPhaseAlgorithm {

	private String modelString;

	/**
	 * This method has to perform the preprocessing.
	 * 
	 * @param data
	 *            the data for the pattern recognition pipeline.
	 * @return
	 * @throws Exception
	 */
	protected abstract Instances filterData(PipelineData data) throws Exception;

	/**
	 * This method has to prepare the complexity analysis. Parameters from the
	 * training process that are necessary for the analysis can be extracted
	 * here.
	 * 
	 * @param data
	 *            the data for the pattern recognition pipeline
	 */
	protected abstract void analyzeSystem(PipelineData data) throws Exception;

	/**
	 * This method returns a string that contains the parameters for the working
	 * phase.
	 */
	protected abstract String getPreprocessingModel(PipelineData data);

	/**
	 * Preprocesses the data and analyzes the complexity.
	 * 
	 * @param data
	 * @return
	 * @throws Exception
	 */
	public Instances preprocess(PipelineData data) throws Exception {
		Instances instances = filterData(data);

		modelString = getPreprocessingModel(data);
		analyzeSystem(data);
		saveAnalysis();

		return instances;
	}

	/**
	 * Returns a string that describes the trained classifier.
	 * 
	 * @return
	 */
	public String getModelString() {
		return modelString;
	}

}
