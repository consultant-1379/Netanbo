package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportElementsMapper {
	
	 @JsonProperty("element")
	 private ArrayList<ReportElements> reportElements  = new ArrayList<>();

	public ArrayList<ReportElements> getReportElements() {
		return reportElements;
	}

	public void setReportElements(ArrayList<ReportElements> reportElements) {
		this.reportElements = reportElements;
	}

	@Override
	public String toString() {
		return "ReportElementsMapper [reportElements=" + reportElements + "]";
	}

}
