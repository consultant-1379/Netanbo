package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertersMapper {
	
	@JsonProperty("alerter")
	private ArrayList<Alerter> alerters = new ArrayList<Alerter>();
	
	public ArrayList<Alerter> getAlerters() {
		return alerters;
	}

	public void setAlerters(ArrayList<Alerter> alerters) {
		this.alerters = alerters;
	}
	
}
