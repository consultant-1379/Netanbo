package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Data {
	
	@JsonProperty("formula")
	private ConditionalFormula formula;

	public ConditionalFormula getFormula() {
		return formula;
	}

	public void setFormula(ConditionalFormula formula) {
		this.formula = formula;
	}
    
	@Override
	public String toString() {
		return "Data [formula=" + formula + "]";
	}
	
	
	
	

}
