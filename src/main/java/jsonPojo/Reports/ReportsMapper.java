package jsonPojo.Reports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jsonPojo.Reports.*;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportsMapper {

    @JsonProperty("report")
    private List<Reports> report = new ArrayList<Reports>();

    public List<Reports> getReports() {
        return report;
    }

    public void setReports(List<Reports> report) {
        this.report = report;
    }


    @Override
    public String toString() {
        return "ReportsMapper{" +
                "report=" + report +
                '}';
    }
}
