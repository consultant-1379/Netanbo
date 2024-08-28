package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlertersWrapper {

	@JsonProperty("alerters")
	private AlertersMapper alerterMapper;

	public AlertersMapper getAlerterMapper() {
		return alerterMapper;
	}

	public void setAlerterMapper(AlertersMapper alerterMapper) {
		this.alerterMapper = alerterMapper;
	}
	
}
