package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {
	
	@JsonProperty("axes")
	private Axes axes;
	
	@JsonProperty("expression")
	private Expression expression;
	
	@JsonProperty("alerters")
	private Alerters alerters;

	public Alerters getAlerters() {
		return alerters;
	}

	public void setAlerters(Alerters alerters) {
		this.alerters = alerters;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public Axes getAxes() {
		return axes;
	}

	public void setAxes(Axes axes) {
		this.axes = axes;
	}

	@Override
	public String toString() {
		return "Content [axes=" + axes + ", expression=" + expression + ", alerters=" + alerters + "]";
	}
	
}
