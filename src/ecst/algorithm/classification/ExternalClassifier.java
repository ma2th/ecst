package ecst.algorithm.classification;

import java.io.File;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.StringTokenizer;

import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import ecst.utilities.FileUtilities;

/**
 * This class implements the integration of external programs as classification
 * algorithms.
 * 
 * @author Matthias Ring
 * 
 */
public class ExternalClassifier extends Classifier {

	private static final long serialVersionUID = 1L;

	private String command;
	private String classifyScript;
	private String trainScript;
	private Instances emptyInstances;
	private Map<String, Integer> externalMultiplier;

	/**
	 * Constructor.
	 */
	public ExternalClassifier() {
	}

	/**
	 * Returns a string description the functions of this classifier.
	 * 
	 * @return
	 */
	public String globalInfo() {
		return "Class for calling an external classifier. " + "The scripts have to be in the subfolder 'files' of the program.\n\n"
				+ "The generated file 'classes.csv' will contain the assignment of the class names and class indices. "
				+ "The two generated files 'instance.format' and 'instances.format' contain format strings "
				+ "for the data files 'instance.csv' and 'instances.csv'. "
				+ "The external classifier can write a file named 'multiplier.csv' that contains the values for "
				+ "algorithm-specific multipliers.";
	}

	/**
	 * Parses the options in WEKA format.
	 */
	public void setOptions(String[] options) throws Exception {
		command = Utils.getOption('E', options);
		classifyScript = Utils.getOption('C', options);
		trainScript = Utils.getOption('T', options);
	}

	/**
	 * Trains the external classifier.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void buildClassifier(Instances data) throws Exception {
		int i = 0;
		Process process = null;
		FileWriter writer = null;
		Enumeration<String> enumeration = null;
		ProcessBuilder builder = null;

		writer = new FileWriter("./files/classes.csv");
		enumeration = data.classAttribute().enumerateValues();
		while (enumeration.hasMoreElements()) {
			writer.write(i + "," + enumeration.nextElement() + "\n");
			i++;
		}
		writer.close();

		emptyInstances = new Instances(data);
		emptyInstances.delete();

		FileUtilities.saveInstancesCSV(data, new File("./files/instances.csv"), new File("./files/instances.format"));

		builder = new ProcessBuilder(buildCommand(trainScript));
		builder.directory(new File("./files/").getAbsoluteFile());
		process = builder.start();
		process.waitFor();

		externalMultiplier = FileUtilities.loadStringIntegerMap(new File("./files/multiplier.csv"));
	}

	/**
	 * Classifies an instances with the external classifier.
	 */
	@Override
	public double classifyInstance(Instance instance) throws Exception {
		Process process = null;
		ProcessBuilder builder = null;
		Scanner scanner = null;
		String lastLine = null;

		emptyInstances.add(instance);
		FileUtilities.saveInstancesCSV(emptyInstances, new File("./files/instance.csv"), new File("./files/instance.format"));
		emptyInstances.delete();

		builder = new ProcessBuilder(buildCommand(classifyScript));
		builder.directory(new File("./files/").getAbsoluteFile());
		process = builder.start();

		scanner = new Scanner(process.getInputStream());
		while (scanner.hasNextLine()) {
			lastLine = scanner.nextLine();

		}
		scanner.close();

		return Double.parseDouble(lastLine);
	}

	/**
	 * Internal method to build to command for the ProcessBuilder object.
	 * 
	 * @param script
	 * @return
	 */
	private List<String> buildCommand(String script) {
		List<String> list = new LinkedList<String>();
		StringTokenizer tokenizer = new StringTokenizer(command, " ");

		while (tokenizer.hasMoreTokens()) {
			list.add(tokenizer.nextToken());
		}
		list.add(script);

		return list;
	}

	/**
	 * Returns the multipliers determined by the external program.
	 * 
	 * @return
	 */
	public Map<String, Integer> getExternalMultiplier() {
		return externalMultiplier;
	}

}
