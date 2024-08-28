package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Rule {
	
	private int id;
	
	private Conditions conditions;
	
	private String expression;

	@Override
	public String toString() {
		return "Rule [id=" + id + ", conditions=" + conditions + ", expression=" + expression + "]";
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Conditions getConditions() {
		return conditions;
	}

	public void setConditions(Conditions conditions) {
		this.conditions = conditions;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}
	
	
	
}
