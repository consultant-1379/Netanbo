package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class VtableExpression {

	private ArrayList<ElementFormula> formula = new ArrayList<>();

	public ArrayList<ElementFormula> getFormula() {
		return formula;
	}

	public void setFormula(ArrayList<ElementFormula> formula) {
		this.formula = formula;
	}

	@Override
	public String toString() {
		return "VtableExpression [formula=" + formula + "]";
	}
	
	
}
