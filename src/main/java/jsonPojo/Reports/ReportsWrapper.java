package jsonPojo.Reports;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonIgnoreProperties(ignoreUnknown=true)
public class ReportsWrapper {

    @JsonProperty("reports")
    private ReportsMapper mapper;

    public ReportsMapper getMapper() {
        return mapper;
    }

    public void setMapper(ReportsMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public String toString() {
        return "ReportsWrapper{" +
                "mapper=" + mapper +
                '}';
    }
}
