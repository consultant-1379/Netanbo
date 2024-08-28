package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Alerters {

	@JsonProperty("id")
	private ArrayList<Integer> alerterId = new ArrayList<>();

	public ArrayList<Integer> getAlerterId() {
		return alerterId;
	}

	public void setAlerterId(ArrayList<Integer> alerterId) {
		this.alerterId = alerterId;
	}

	@Override
	public String toString() {
		return "Alerters [alerterId=" + alerterId + "]";
	}
	
	
}
