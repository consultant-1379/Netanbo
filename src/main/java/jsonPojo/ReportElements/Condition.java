package jsonPojo.ReportElements;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Condition {
	
	@JsonProperty("@expressionId")
	private String expressionId;
	
	@JsonProperty("@operator")
	private String operator;
	
	@JsonProperty("operand")
	private List<String> operand = new ArrayList<String>();

	public String getExpressionId() {
		return expressionId;
	}

	public void setExpressionId(String expressionId) {
		this.expressionId = expressionId;
	}

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public List<String> getOperand() {
		return operand;
	}

	public void setOperand(List<String> operand) {
		this.operand = operand;
	}

	@Override
	public String toString() {
		return "Condition [expressionId=" + expressionId + ", operator=" + operator + ", operand=" + operand + "]";
	} 
	
}
