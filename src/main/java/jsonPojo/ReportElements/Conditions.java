package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Conditions {
	
	private ArrayList<Condition> condition = new ArrayList<>();

	public ArrayList<Condition> getCondition() {
		return condition;
	}

	public void setCondition(ArrayList<Condition> condition) {
		this.condition = condition;
	}

	@Override
	public String toString() {
		return "Conditions [condition=" + condition + "]";
	}
	
	
	
}
