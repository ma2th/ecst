package ecst.featureextraction;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.LineNumberReader;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import ecst.algorithm.FeatureExtractionAlgorithm;
import ecst.utilities.FileUtilities;

/**
 * This class performs the feature extraction.
 * 
 * @author Matthias Ring
 * 
 */
public class FeatureExtractor {

	/**
	 * Start method for feature extraction
	 * 
	 * @param model
	 *            the settings for feature extraction
	 * @param featureExtractionAlgorithms
	 *            the algorithm that the user selected
	 * @return the extracted features
	 * @throws Exception
	 */
	public static Instances extract(FeatureExtractionModel model, List<FeatureExtractionAlgorithm> featureExtractionAlgorithms) throws Exception {
		int lineCounter;
		String line = null;
		double[][] data = null;
		int numberOfInputColumns;
		Instances instances = null;
		BufferedReader reader = null;
		FastVector classLabels = null;
		FastVector ids = null;
		int numberOfLinesPerFeature;
		List<String> lastLines = new LinkedList<String>();

		// init objects
		classLabels = initClassLabels(model.getInputFiles());
		ids = initSubjectIDs(model.getInputFiles());
		numberOfInputColumns = getNumberOfInputColumns(model.getInputFiles().get(0), model.getDelimiter());
		
		// get number of lines in source files
		if (model.getInputLinesForOneFeature() == null) {
			numberOfLinesPerFeature = -1;//means that it depends on the size of the file as the complete file is used
			instances = initInstances(numberOfInputColumns, getLinesOfLargestFile(model), featureExtractionAlgorithms, classLabels, ids);
			//numberOfLinesPerFeature = getLinesOfLargestFile(model);
			model.setInputLinesForOneFeature(getLinesOfLargestFile(model));
		} else {
			numberOfLinesPerFeature = model.getInputLinesForOneFeature();
			instances = initInstances(numberOfInputColumns, numberOfLinesPerFeature, featureExtractionAlgorithms, classLabels, ids);
		}

		// extract feature for each source file
		for (InputFile inputFile : model.getInputFiles()) {
			lineCounter = 0;
			lastLines.clear();
			reader = new BufferedReader(new FileReader(inputFile.getFilename()));
			while ((line = reader.readLine()) != null) {
				//ignore blank lines
				if (line.trim().length() != 0){
					lastLines.add(line);
					lineCounter++;
					if (lineCounter == numberOfLinesPerFeature) {
						data = parseBlock(numberOfInputColumns, lineCounter, lastLines, model.getDelimiter());
						extractFeatures(data, featureExtractionAlgorithms, instances, inputFile);
						lineCounter = 0;
						lastLines.clear();
					}
				}
			}
			if (numberOfLinesPerFeature == -1){
				data = parseBlock(numberOfInputColumns, lineCounter, lastLines, model.getDelimiter());
				extractFeatures(data, featureExtractionAlgorithms, instances, inputFile);				
			}
			//only extract features that have the complete number of lines per feature
			//if (lineCounter > 0) {
			//	data = parseBlock(numberOfInputColumns, lineCounter, lastLines, model.getDelimiter());
			//	extractFeatures(data, featureExtractionAlgorithms, instances, inputFile);
			//}
			reader.close();
		}

		// save extracted features to file
		if (model.getOutputFile() != null && !model.getOutputFile().equals("")) {
			FileUtilities.saveInstances(instances, new File(model.getOutputFile()));
		}

		return instances;
	}

	/**
	 * Returns the number of lines in the largest file.
	 * 
	 * @param model
	 * @return
	 * @throws Exception
	 */
	private static int getLinesOfLargestFile(FeatureExtractionModel model) throws Exception {
		int max = Integer.MIN_VALUE;
		LineNumberReader reader = null;

		for (InputFile inputFile : model.getInputFiles()) {
			reader = new LineNumberReader(new FileReader(inputFile.getFilename()));
			while (reader.readLine() != null)
				;
			if (reader.getLineNumber() > max) {
				max = reader.getLineNumber();
			}
			reader.close();
		}
		return max;
	}

	/**
	 * Returns the first line of the given file.
	 * 
	 * @param inputFile
	 * @return
	 * @throws Exception
	 */
	private static String getExampleLine(InputFile inputFile) throws Exception {
		String line = null;
		BufferedReader reader = null;

		reader = new BufferedReader(new FileReader(inputFile.getFilename()));
		line = reader.readLine();
		reader.close();
		return line;
	}

	/**
	 * Returns the number of columns in the given input file.
	 * 
	 * @param inputFile
	 * @param delimiter
	 * @return
	 * @throws Exception
	 */
	public static int getNumberOfInputColumns(InputFile inputFile, String delimiter) throws Exception {
		StringTokenizer tokenizer = null;

		tokenizer = new StringTokenizer(getExampleLine(inputFile), delimiter);
		return tokenizer.countTokens();
	}

	/**
	 * Initializes the FastVector object for WEKA with the class labels.
	 * 
	 * @param inputFiles
	 * @return
	 */
	private static FastVector initClassLabels(List<InputFile> inputFiles) {
		FastVector labels = new FastVector();

		for (InputFile inputFile : inputFiles) {
			if (!labels.contains(inputFile.getClassLabel())) {
				labels.addElement(inputFile.getClassLabel());
			}
		}
		return labels;
	}

	/**
	 * Initializes a FastVector object containing every subject exactly once.
	 * 
	 * @param inputFiles
	 * @return
	 */
	private static FastVector initSubjectIDs(List<InputFile> inputFiles) {
		FastVector ids = new FastVector();

		for (InputFile inputFile : inputFiles) {
			if (!ids.contains("" + inputFile.getSubjectID())) {
				ids.addElement("" + inputFile.getSubjectID());
			}
		}
		return ids;
	}

	/**
	 * Creates the instances object: creates the columns, class label and
	 * subject id.
	 * 
	 * @param numberOfColumns
	 * @param numberOfLines
	 * @param featureExtractionAlgorithms
	 * @param classLabels
	 * @param ids
	 * @return
	 * @throws Exception
	 */
	private static Instances initInstances(int numberOfColumns, int numberOfLines, List<FeatureExtractionAlgorithm> featureExtractionAlgorithms,
			FastVector classLabels, FastVector ids) throws Exception {
		int currentNumberOfFeatures;
		String attributeName = null;
		Attribute attribute = null;
		Instances instances = null;
		FastVector vector = new FastVector();

		for (FeatureExtractionAlgorithm algorithm : featureExtractionAlgorithms) {
			currentNumberOfFeatures = algorithm.getNumberOfFeatures(numberOfColumns, numberOfLines);
			for (int i = 0; i < currentNumberOfFeatures; i++) {
				attributeName = algorithm.toString();
				if (i > 0) {
					attributeName += " " + (i + 1) + "/" + currentNumberOfFeatures;
				}
				attribute = new Attribute(attributeName);
				attribute.addRelation(instances);
				vector.addElement(attribute);
			}
		}
		attribute = new Attribute("Subject ID", ids);
		attribute.addRelation(instances);
		vector.addElement(attribute);
		attribute = new Attribute("Class label", classLabels);
		attribute.addRelation(instances);
		vector.addElement(attribute);

		instances = new Instances("Extracted features", vector, 0);
		instances.setClassIndex(vector.size() - 1);

		return instances;
	}

	/**
	 * Converts a line from the input file into a double array.
	 * 
	 * @param numberOfColumns
	 * @param lineCounter
	 * @param lastLines
	 * @param delimiter
	 * @return
	 * @throws Exception
	 */
	private static double[][] parseBlock(int numberOfColumns, int lineCounter, List<String> lastLines, String delimiter) throws Exception {
		int currentLine;
		int currentColumn;
		String token = null;
		StringTokenizer tokenizer = null;
		double[][] data = new double[numberOfColumns][lineCounter];

		currentLine = 0;
		for (String lastLine : lastLines) {
			try {
				tokenizer = new StringTokenizer(lastLine, delimiter);
				currentColumn = 0;
				while (tokenizer.hasMoreTokens()) {
					token = tokenizer.nextToken();
					data[currentColumn][currentLine] = Double.parseDouble(token);
					currentColumn++;
				}
				currentLine++;
			} catch (Exception e) {
				throw new Exception("Cannot parse token '" + token + "' in line: \n" + lastLine + "\nError message: " + e.getMessage() + "\n\nPlease check the data format!");
			}
		}
		return data;
	}

	/**
	 * Extracts the feature by calling the corresponding feature extraction
	 * algorithms and saves the result into the instances object.
	 * 
	 * @param data
	 * @param featureExtractionAlgorithms
	 * @param instances
	 * @param inputFile
	 */
	private static void extractFeatures(double[][] data, List<FeatureExtractionAlgorithm> featureExtractionAlgorithms, Instances instances, InputFile inputFile) {
		int position = 0;
		Instance instance = null;
		double[] features = null;
		double[] allFeatures = new double[instances.numAttributes() - 1];
		String[] attributeNames = null;

		for (FeatureExtractionAlgorithm algorithm : featureExtractionAlgorithms) {
			features = algorithm.extractFeatures(data);
			attributeNames = new String[features.length];
			for (int i = 0; i < attributeNames.length; i++) {
				attributeNames[i] = instances.attribute(i + position).name();
			}
			algorithm.setDependsOnFeatures(attributeNames);
			System.arraycopy(features, 0, allFeatures, position, features.length);
			position += features.length;
		}

		instance = new Instance(instances.numAttributes());
		instance.setDataset(instances);
		for (int i = 0; i < allFeatures.length; i++) {
			instance.setValue(i, allFeatures[i]);
		}
		instance.setValue(instance.numAttributes() - 2, "" + inputFile.getSubjectID());
		instance.setValue(instance.numAttributes() - 1, inputFile.getClassLabel());
		instances.add(instance);
	}

}
