package jsonPojo.dataProviders.Tables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ReportMapper {

    @JsonIgnoreProperties(ignoreUnknown=true)
    public class ReportsMapper {

        @JsonProperty("report")
        private List<Report> report = new ArrayList<Report>();

        public List<Report> getReports() {
            return report;
        }

        public void setReports(List<Report> report) {
            this.report = report;
        }


        @Override
        public String toString() {
            return "ReportsMapper{" +
                    "report=" + report +
                    '}';
        }
    }

}
