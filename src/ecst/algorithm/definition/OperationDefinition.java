package ecst.algorithm.definition;

/**
 * This class represents the operation defintion from the XML configuration
 * document.
 * 
 * @author Matthias Ring
 * 
 */
public class OperationDefinition {

	private String name;
	private String desciptionHTML;
	private String descriptionLatex;

	public OperationDefinition(String name, String desciptionHTML, String descriptionLatex) {
		this.name = name;
		this.desciptionHTML = desciptionHTML;
		this.descriptionLatex = descriptionLatex;
	}

	public String toString() {
		return name + " " + desciptionHTML;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesciptionHTML() {
		return desciptionHTML;
	}

	public void setDesciptionHTML(String desciptionHTML) {
		this.desciptionHTML = desciptionHTML;
	}

	public String getDescriptionLatex() {
		return descriptionLatex;
	}

	public void setDescriptionLatex(String descriptionLatex) {
		this.descriptionLatex = descriptionLatex;
	}

	@Override
	public boolean equals(Object operationDefinition) {
		if (!(operationDefinition instanceof OperationDefinition)) {
			return false;
		}

		OperationDefinition definition = (OperationDefinition) operationDefinition;
		if (definition.getName().equals(getName()) && definition.getDesciptionHTML().equals(getDesciptionHTML())
				&& definition.getDescriptionLatex().equals(getDescriptionLatex())) {
			return true;
		} else {
			return false;
		}
	}

}
