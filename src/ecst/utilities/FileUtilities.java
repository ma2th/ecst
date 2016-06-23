package ecst.utilities;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Scanner;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;

/**
 * This class contains various methods for file handling.
 * 
 * @author Matthias Ring
 * 
 */
public class FileUtilities {

	public static final String CSV_DELIMITER = ",";
	private static final String XML_SUFFIX = ".xml";
	private static final String ARFF_SUFFIX = ".arff";
	private static final String LATEX_SUFFIX = ".tex";
	private static final String CSV_SUFFIX = ".csv";
	private static final String XML_DESCRIPTION = "XML - Extensible Markup Language";
	private static final String ARFF_DESCRIPTION = "ARFF - Attribute Relation File Format";
	private static final String LATEX_DESCRIPTION = "Latex Table";
	private static final String CSV_DESCRIPTION = "Comma Separated Values";

	/**
	 * Reads the file into a Properties object.
	 */
	public static Properties loadPropertiesFile(String filename) throws IOException {
		Properties properties = null;
		BufferedInputStream stream = null;

		properties = new Properties();
		try {
			stream = new BufferedInputStream(new FileInputStream(filename));
			properties.load(stream);
			stream.close();
		} catch (FileNotFoundException e) {
			// is ok
		}

		return properties;
	}

	/**
	 * Writes the properties object into the given file.
	 * 
	 * @param properties
	 * @param filename
	 */
	public static void savePropertiesFile(Properties properties, String filename) {
		try {
			properties.store(new FileOutputStream(filename), null);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String exportCSVString(String csv) {
		if (csv != null && csv.contains(CSV_DELIMITER)) {
			return "\"" + csv + "\"";
		} else if (csv != null) {
			return csv;
		} else
			return "";
	}

	public static String exportXMLString(String xml) {
		return xml.replaceAll("&", "&amp;").replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("\"", "&qout;")
				.replaceAll("'", "&apos;");
	}

	public static String exportLatexString(String latex) {
		return latex.replaceAll("&", "\\\\&").replaceAll("%", "\\\\%").replaceAll("_", "\\\\_");
	}

	/**
	 * Loads one object from the given file.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Object loadObject(File file) throws Exception {
		Object obj = null;
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
		obj = ois.readObject();
		ois.close();
		return obj;
	}

	/**
	 * Saves one object into the given file.
	 * 
	 * @param object
	 * @param file
	 * @throws Exception
	 */
	public static void saveObject(Object object, File file) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
		oos.writeObject(object);
		oos.close();
	}

	/**
	 * Writes the given string to the given file.
	 * 
	 * @param text
	 * @param file
	 * @throws Exception
	 */
	public static void saveString(String text, File file) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		writer.write(text);
		writer.close();
	}

	public static void saveUTF8String(String text, File file) throws Exception {
		ByteBuffer buffer = Charset.forName("UTF-8").encode(text);
		@SuppressWarnings("resource")
		FileChannel out = new FileOutputStream(file).getChannel();
		out.write(buffer);
		out.close();
	}

	/**
	 * Saves the given instances into the given file.
	 * 
	 * @param instances
	 * @param file
	 * @throws Exception
	 */
	public static void saveInstances(Instances instances, File file) throws Exception {
		ArffSaver saver = new ArffSaver();

		saver.setInstances(instances);
		saver.setFile(file);
		saver.writeBatch();
	}

	/**
	 * Saves the given instances into the given csv file.
	 * 
	 * @param instances
	 * @param file
	 * @param formatFile
	 * @throws Exception
	 */
	public static void saveInstancesCSV(Instances instances, File file, File formatFile) throws Exception {
		String format = "";
		BufferedWriter bufferedWriter = null;
		CSVSaver saver = new CSVSaver();

		saver.setInstances(instances);
		saver.setFile(file);
		saver.writeBatch();

		if (formatFile != null) {
			bufferedWriter = new BufferedWriter(new FileWriter(formatFile));
			for (int i = 0; i < instances.numAttributes() - 1; i++) {
				format += "%f ";
			}
			format += "%s";
			bufferedWriter.write(format);
			bufferedWriter.close();
		}
	}

	/**
	 * Reads a file that contains two columns: one column with a string and one
	 * column with an integer.
	 * 
	 * @param file
	 * @return
	 * @throws Exception
	 */
	public static Map<String, Integer> loadStringIntegerMap(File file) throws Exception {
		Map<String, Integer> map = new HashMap<String, Integer>();
		Scanner scanner = new Scanner(file);
		String line = null;

		while (scanner.hasNextLine()) {
			line = scanner.nextLine();
			map.put(line.substring(0, line.indexOf(",")).trim(),
					Integer.parseInt(line.substring(line.indexOf(",") + 1).trim()));
		}
		scanner.close();

		return map;
	}

	/**
	 * Shows an JFileChooser for opening of ARFF files.
	 * 
	 * @param parent
	 * @param startingFile
	 * @return
	 */
	public static File showOpenFileChooserARFF(JComponent parent, String startingFile) {
		return openFileChooser(parent, ARFF_DESCRIPTION, ARFF_SUFFIX, startingFile);
	}

	/**
	 * Shows an JFileChooser for opening of XML files.
	 * 
	 * @param parent
	 * @param startingFile
	 * @return
	 */
	public static File showOpenFileChooserXML(JComponent parent, String startingFile) {
		return openFileChooser(parent, XML_DESCRIPTION, XML_SUFFIX, startingFile);
	}

	public static File showSaveFileChooserXML(JComponent parent, String startingFile) {
		return saveFileChooser(parent, XML_DESCRIPTION, XML_SUFFIX, startingFile);
	}

	public static File showSaveFileChooserCSV(JComponent parent, String startingFile) {
		return saveFileChooser(parent, CSV_DESCRIPTION, CSV_SUFFIX, startingFile);
	}

	public static File showSaveFileChooserLatex(JComponent parent, String startingFile) {
		return saveFileChooser(parent, LATEX_DESCRIPTION, LATEX_SUFFIX, startingFile);
	}

	public static File showSaveFileChooserARFF(JComponent parent, String startingFile) {
		return saveFileChooser(parent, ARFF_DESCRIPTION, ARFF_SUFFIX, startingFile);
	}

	/**
	 * Shows a JFileChoser for opening with the given configuration.
	 * 
	 * @param parent
	 * @param description
	 * @param suffix
	 * @param startingFile
	 * @return
	 */
	private static File openFileChooser(JComponent parent, String description, String suffix, String startingFile) {
		JFileChooser fileChooser = new JFileChooser(startingFile);

		fileChooser.setFileFilter(createFileFilter(description, suffix));
		if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Shows a JFileChoser for saving with the given configuration.
	 * 
	 * @param parent
	 * @param description
	 * @param suffix
	 * @param startingFile
	 * @return
	 */
	private static File saveFileChooser(JComponent parent, String description, String suffix, String startingFile) {
		String selectedFile = null;
		JFileChooser fileChooser = new JFileChooser(startingFile);

		fileChooser.setFileFilter(createFileFilter(description, suffix));
		if (fileChooser.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile().getAbsolutePath();
			if (!selectedFile.endsWith(suffix)) {
				return new File(selectedFile + suffix);
			}
			return fileChooser.getSelectedFile();
		}
		return null;
	}

	/**
	 * Creates the FileFilter object for the JFileChooser with the given
	 * configuration.
	 * 
	 * @param description
	 * @param suffix
	 * @return
	 */
	private static FileFilter createFileFilter(final String description, final String suffix) {
		return new FileFilter() {
			@Override
			public String getDescription() {
				return description;
			}

			@Override
			public boolean accept(File file) {
				if (file.isDirectory() || file.getAbsolutePath().endsWith(suffix)) {
					return true;
				}
				return false;
			}
		};
	}
}
