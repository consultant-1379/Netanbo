package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlerterFormatDetailsMapper {

	@JsonProperty("alerter")
	private AlerterFormatDetails formatDetails;

	public AlerterFormatDetails getFormatDetails() {
		return formatDetails;
	}

	public void setFormatDetails(AlerterFormatDetails formatDetails) {
		this.formatDetails = formatDetails;
	}

	@Override
	public String toString() {
		return "AlerterFormatDetailsMapper [formatDetails=" + formatDetails + "]";
	}
	
	

}
