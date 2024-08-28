package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlerterDetailsMapper {

	@JsonProperty("alerter")
	AlerterDetails alerter;

	public AlerterDetails getAlerter() {
		return alerter;
	}

	public void setAlerter(AlerterDetails alerter) {
		this.alerter = alerter;
	}
}
