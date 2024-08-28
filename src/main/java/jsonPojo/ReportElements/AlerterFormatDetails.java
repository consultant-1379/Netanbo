package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlerterFormatDetails {

	@JsonProperty("rule")
	private ArrayList<FormatRule> rule = new ArrayList<>();

	public ArrayList<FormatRule> getRule() {
		return rule;
	}

	public void setRule(ArrayList<FormatRule> rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		
		return "AlerterFormatDetails [rule=" + rule + "]";
	}

	
	
}
