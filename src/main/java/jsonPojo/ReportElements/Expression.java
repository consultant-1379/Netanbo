package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Expression {

	@JsonProperty("formula")
	private ElementFormula formula;
	
	@JsonProperty("format")
	private Format format;

	public Format getFormat() {
		return format;
	}

	public void setFormat(Format format) {
		this.format = format;
	}

	public ElementFormula getFormula() {
		return formula;
	}

	public void setFormula(ElementFormula formula) {
		this.formula = formula;
	}

	@Override
	public String toString() {
		return "Expression [formula=" + formula + ", format=" + format + "]";
	}
}
