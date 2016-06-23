package ecst.dataconverter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * This class converts the data from the adidas_1 project into an ARFF file
 * format.
 * 
 * @author Matthias Ring
 * 
 */
public class Adidas1Converter {

	/**
	 * Search the corresponding feature and labels files.
	 * 
	 * @param folder
	 * @return
	 */
	private static List<Pair<File, File>> searchPairs(String folder) {
		String subjectName = null;
		File sourceFolder = null;
		List<Pair<File, File>> pairs = new LinkedList<Pair<File, File>>();

		sourceFolder = new File(folder);
		for (File featureFile : sourceFolder.listFiles()) {
			if (featureFile.getName().endsWith("features.txt")) {
				subjectName = featureFile.getName().substring(0, featureFile.getName().lastIndexOf("_"));
				for (File labelsFile : sourceFolder.listFiles()) {
					if (labelsFile.getName().endsWith("labels.txt")
							&& labelsFile.getName().substring(0, labelsFile.getName().lastIndexOf("_")).equals(subjectName)) {
						pairs.add(new Pair<File, File>(featureFile, labelsFile));
					}
				}
			}
		}

		return pairs;
	}

	/**
	 * Combines the two different files into one.
	 * 
	 * @param pairs
	 * @throws Exception
	 */
	private static List<String> combineFeaturesWithLabels(List<Pair<File, File>> pairs, String path) throws Exception {
		int id = 0;
		String featureLine = null;
		String labelsLine = null;
		String subjectName = null;
		String filename = null;
		BufferedReader featureReader = null;
		BufferedReader labelsReader = null;
		BufferedWriter resultWriter = null;
		List<String> files = new LinkedList<String>();

		for (Pair<File, File> pair : pairs) {
			subjectName = pair.getFirst().getName().substring(0, pair.getFirst().getName().lastIndexOf("_"));
			featureReader = new BufferedReader(new FileReader(pair.getFirst()));
			labelsReader = new BufferedReader(new FileReader(pair.getSecond()));
			filename = path + "/" + subjectName + ".csv";
			files.add(filename);
			resultWriter = new BufferedWriter(new FileWriter(filename));

			System.out.println("Writing: " + filename);
			while ((featureLine = featureReader.readLine()) != null) {
				labelsLine = labelsReader.readLine().trim();
				featureLine = featureLine.replaceAll("\\s+", ", ");
				featureLine = featureLine.replaceAll("^,\\s*", "");
				featureLine = featureLine.replaceAll(",\\s*$", "");
				if (labelsLine.equals("-1")) {
					resultWriter.write(featureLine + ", " + id + ", hard\n");
				} else {
					resultWriter.write(featureLine + ", " + id + ", soft\n");
				}
			}

			id++;

			featureReader.close();
			labelsReader.close();
			resultWriter.close();
		}

		return files;
	}

	/**
	 * Creates a single ARFF file from the intermediate CSV files.
	 * 
	 * @throws Exception
	 */
	private static void createARFFFile(List<String> files, String path) throws Exception {
		String line = null;
		BufferedReader reader = null;
		BufferedWriter writer = new BufferedWriter(new FileWriter(path + "/adidas_1.arff"));

		System.out.println("Writing: " + path + "/adidas_1.arff");

		writer.write("@relation adidas_1\n");
		writer.write("@attribute 'Step compression first order least-squares fit' real\n");
		writer.write("@attribute 'Step decompression first order least-squares fit' real\n");
		writer.write("@attribute 'Time between step compression maxima points' real\n");
		writer.write("@attribute 'Time from step compression maximum to step end' real\n");
		writer.write("@attribute 'Time from step start to step compression maximum' real\n");
		writer.write("@attribute 'Step curve area approximation by Trapezoid method' real\n");
		writer.write("@attribute 'Time from step start to step end' real\n");
		writer.write("@attribute 'Step mean value' real\n");
		writer.write("@attribute 'Step median value' real\n");
		writer.write("@attribute 'Step compression maximum value' real\n");
		writer.write("@attribute 'SD of the values contained in one step' real\n");
		writer.write("@attribute 'SD of the step minima (feature 10)' real\n");
		writer.write("@attribute 'SD of the step means (feature 8)' real\n");
		writer.write("@attribute 'SD of the step standard deviation (feature 11)' real\n");
		writer.write("@attribute 'SD of the step duration (feature 7)' real\n");
		writer.write("@attribute 'SD of the step area (feature 6)' real\n");
		writer.write("@attribute 'SD of the time between steps (feature 3)' real\n");
		writer.write("@attribute 'SD of the time to peak (feature 5)' real\n");
		writer.write("@attribute 'SD of the time from peak (feature 4)' real\n");
		writer.write("@attribute 'Subject ID' {");
		for (int i = 0; i < files.size() - 1; i++) {
			writer.write(i + ", ");
		}
		writer.write("" + (files.size() - 1));
		writer.write("}\n");
		writer.write("@attribute 'class' {hard, soft}\n");
		writer.write("@data\n");

		for (String file : files) {
			reader = new BufferedReader(new FileReader(file));
			while ((line = reader.readLine()) != null) {
				writer.write(line + "\n");
			}
			reader.close();
		}
		writer.close();
	}

	/**
	 * Main.
	 * 
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		List<String> files = null;
		List<Pair<File, File>> pairs = null;

		if (args.length != 1) {
			System.out.println("Usage: java " + Adidas1Converter.class.getCanonicalName() + " <data directory>");
			System.exit(-1);
		}

		pairs = searchPairs(args[0]);
		files = combineFeaturesWithLabels(pairs, args[0]);
		createARFFFile(files, args[0]);

		System.out.println("Successfully finished.");
	}

}
