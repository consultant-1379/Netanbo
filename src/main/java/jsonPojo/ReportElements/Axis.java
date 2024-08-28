package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Axis {
	
	@JsonProperty("expressions")
	private VtableExpression expression;

	public VtableExpression getExpression() {
		return expression;
	}

	public void setExpression(VtableExpression expression) {
		this.expression = expression;
	}

	@Override
	public String toString() {
		return "Axis [expression=" + expression + "]";
	}
	
	
}
