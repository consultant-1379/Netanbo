package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportElementsWrapper {
	
	@JsonProperty("elements")
	private ReportElementsMapper reportElementMapper;

	public ReportElementsMapper getReportElementMapper() {
		return reportElementMapper;
	}

	public void setReportElementMapper(ReportElementsMapper reportElementMapper) {
		this.reportElementMapper = reportElementMapper;
	}

}
