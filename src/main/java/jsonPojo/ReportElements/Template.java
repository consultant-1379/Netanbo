package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Template {
	
	@JsonProperty("@positive")
	private String positive;
	
	@JsonProperty("@negative")
	private String negative;

	public String getPositive() {
		return positive;
	}

	public void setPositive(String positive) {
		this.positive = positive;
	}

	public String getNegative() {
		return negative;
	}

	public void setNegative(String negative) {
		this.negative = negative;
	}

	@Override
	public String toString() {
		return "Template [positive=" + positive + ", negative=" + negative + "]";
	}
	

}
