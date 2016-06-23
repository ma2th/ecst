package ecst.algorithm.classification;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import weka.classifiers.Classifier;
import weka.classifiers.functions.LibSVM;
import weka.classifiers.meta.GridSearch;
import weka.core.Utils;
import weka.filters.AllFilter;
import ecst.algorithm.parameter.Parameter;
import ecst.algorithm.parameter.SelectedParameter;
import ecst.algorithm.parameter.SelectedParameterItem;
import ecst.utilities.ParameterUtilities;

/**
 * This class manages stuff that is necessary for a grid search.
 * 
 * @author Matthias Ring
 * 
 */
public class GridSearchManager {

	private Parameter enableGridSearch;
	private Parameter gridX;
	private Parameter minX;
	private Parameter maxX;
	private Parameter stepX;
	private Parameter baseX;
	private Parameter expressionX;
	private Parameter gridY;
	private Parameter minY;
	private Parameter maxY;
	private Parameter stepY;
	private Parameter baseY;
	private Parameter expressionY;
	private Parameter seed;
	private Parameter maxGridExtensions;
	private Parameter sampleSize;
	private Parameter expandableGrid;
	private Parameter traversal;
	private SelectedParameterItem rowWise;
	private SelectedParameterItem columnWise;
	private Object gridSearchXValue;
	private Object gridSearchYValue;
	private Parameter evaluation;
	private SelectedParameterItem correlationCoefficient;
	private SelectedParameterItem rootMeanSquaredError;
	private SelectedParameterItem rootRelativeSquaredError;
	private SelectedParameterItem meanAbsoluteError;
	private SelectedParameterItem rootAbsoluteError;
	private SelectedParameterItem combined;
	private SelectedParameterItem accuracy;
	private SelectedParameterItem kappa;

	/**
	 * Constructor.
	 * 
	 * @param algorithmParameters the grid search paramters.
	 */
	public GridSearchManager(Parameter[] algorithmParameters) {
		List<SelectedParameterItem> list = null;

		rowWise = new SelectedParameterItem("Row wise", "-traversal ROW-WISE");
		columnWise = new SelectedParameterItem("Column wise", "-traversal COLUMN-WISE");
		correlationCoefficient = new SelectedParameterItem("Correlation coefficient", "-E CC");
		rootMeanSquaredError = new SelectedParameterItem("Root mean squared error", "-E RMSE");
		rootRelativeSquaredError = new SelectedParameterItem("Root relative squared error", "-E RRSE");
		meanAbsoluteError = new SelectedParameterItem("Mean absolute error", "-E MAE");
		rootAbsoluteError = new SelectedParameterItem("Root absolute error", "-E RAE");
		combined = new SelectedParameterItem("Combined: (1-abs(CC)) + RRSE + RAE", "-E COMB");
		accuracy = new SelectedParameterItem("Accuracy", "-E ACC");
		kappa = new SelectedParameterItem("Kappa", "-E KAP");

		enableGridSearch = new Parameter(false, "Enable grid search", Parameter.TYPE.BOOLEAN, null);
		expandableGrid = new Parameter(false, "Expandable grid", Parameter.TYPE.BOOLEAN, "-extend-grid");
		maxGridExtensions = new Parameter(3, "Maximum number of grid extensions", Parameter.TYPE.INTEGER, "-max-grid-extensions");
		sampleSize = new Parameter(100.0, "Percent of samples to search the initial grid", Parameter.TYPE.DOUBLE, "-sample-size");
		seed = new Parameter(1, "Random number seed", Parameter.TYPE.INTEGER, "-S");
		minX = new Parameter(5.0, "Minimum x", Parameter.TYPE.DOUBLE, "-x-min");
		maxX = new Parameter(20.0, "Maximum x", Parameter.TYPE.DOUBLE, "-x-max");
		stepX = new Parameter(1.0, "Step x", Parameter.TYPE.DOUBLE, "-x-step");
		baseX = new Parameter(10.0, "Base x", Parameter.TYPE.DOUBLE, "-x-base");
		expressionX = new Parameter("I", "Expression x", Parameter.TYPE.STRING, "-x-expression");
		minY = new Parameter(-10.0, "Minimum y", Parameter.TYPE.DOUBLE, "-y-min");
		maxY = new Parameter(5.0, "Maximum y", Parameter.TYPE.DOUBLE, "-y-max");
		stepY = new Parameter(1.0, "Step y", Parameter.TYPE.DOUBLE, "-y-step");
		baseY = new Parameter(10.0, "Base y", Parameter.TYPE.DOUBLE, "-y-base");
		expressionY = new Parameter("I", "Expression y", Parameter.TYPE.STRING, "-y-expression");
		traversal = ParameterUtilities.createSelectedParameter("Grid traversal", columnWise, rowWise);
		evaluation = ParameterUtilities.createSelectedParameter("Evaluation method", accuracy, correlationCoefficient,
				rootMeanSquaredError, rootRelativeSquaredError, meanAbsoluteError, rootAbsoluteError, combined, kappa);

		list = new LinkedList<SelectedParameterItem>();
		for (Parameter parameter : algorithmParameters) {
			list.add(new SelectedParameterItem(parameter.getName(), "-x-property " + parameter.getGridSearchString()));
		}
		gridX = new Parameter(new SelectedParameter(list, 0), "Parameter x", Parameter.TYPE.SELECTED_PARAMETER, null);

		list = new LinkedList<SelectedParameterItem>();
		for (Parameter parameter : algorithmParameters) {
			list.add(new SelectedParameterItem(parameter.getName(), "-y-property " + parameter.getGridSearchString()));
		}
		gridY = new Parameter(new SelectedParameter(list, 0), "Parameter y", Parameter.TYPE.SELECTED_PARAMETER, null);
	}

	/**
	 * Returns the class that implements the grid search.
	 * 
	 * @return
	 */
	public Class<? extends Object> getImplementingClass() {
		return GridSearch.class;
	}

	/**
	 * Creates a new instance of the classifier with an grid search.
	 * 
	 * @param algorithmClassName
	 * @param algorithmParameters
	 * @return
	 * @throws Exception
	 */
	public Classifier createClassifier(String algorithmClassName, Parameter[] algorithmParameters) throws Exception {
		String options = null;
		Classifier classifier = null;

		classifier = new GridSearch();
		options = ParameterUtilities.buildOptionsString(getParameters());
		options += " -filter " + AllFilter.class.getCanonicalName() + " ";
		options += " -W " + algorithmClassName + " -- ";
		options += ParameterUtilities.buildOptionsString(algorithmParameters);
		classifier.setOptions(Utils.splitOptions(options));

		return classifier;
	}

	/**
	 * Saves the results of the grid search.
	 * 
	 * @param classifier
	 * @throws Exception
	 */
	public void saveResult(Classifier classifier) throws Exception {
		gridSearchXValue = getResult(gridX, classifier);
		gridSearchYValue = getResult(gridY, classifier);
	}

	/**
	 * Returns the parameter that determines if the user wants to perform a grid
	 * search.
	 */
	public Parameter getEnableGridSearch() {
		return enableGridSearch;
	}

	/**
	 * Returns the first parameter for the grid search.
	 * 
	 * @return
	 */
	public Parameter getGridX() {
		return gridX;
	}

	/**
	 * Returns the second parameter for the grid search.
	 * 
	 * @return
	 */
	public Parameter getGridY() {
		return gridY;
	}

	/**
	 * Returns the result of the first grid search parameter.
	 * 
	 * @return
	 */
	public Object getGridSearchXValue() {
		return gridSearchXValue;
	}

	/**
	 * Returns the result of the second grid search parameter.
	 * 
	 * @return
	 */
	public Object getGridSearchYValue() {
		return gridSearchYValue;
	}

	/**
	 * Returns the parameters that are necessary to perform a grid search.
	 * 
	 * @return
	 */
	public Parameter[] getParameters() {
		return new Parameter[] { gridX, gridY, expandableGrid, evaluation, minX, maxX, stepX, baseX, expressionX, minY, maxY, stepY, baseY,
				expressionY, seed, maxGridExtensions, sampleSize, traversal };
	}

	/**
	 * Internal method to read out the result of the grid search.
	 * 
	 * @param parameter
	 * @param classifier
	 * @return
	 * @throws Exception
	 */
	private Object getResult(Parameter parameter, Classifier classifier) throws Exception {
		SelectedParameter item = null;
		String field = null;
		Method method = null;
		Object result = null;

		item = (SelectedParameter) parameter.getValue();
		field = item.getItems().get(item.getSelectedIndex()).getOptionString();
		field = field.substring(field.indexOf(".") + 1);
		field = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
		method = LibSVM.class.getDeclaredMethod(field, new Class[] {});
		result = method.invoke(classifier, new Object[] {});

		return result;
	}

}
