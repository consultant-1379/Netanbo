package jsonPojo.charts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Chart {

    @JsonProperty("_name")
    private
    String chartANme;

    @JsonProperty("PAGE_BODY")
    private PageBody pageBody;

    public String getChartANme() {
        return chartANme;
    }

    public void setChartANme(String chartANme) {
        this.chartANme = chartANme;
    }


    @Override
    public String toString() {
        return "Chart{" +
                "chartANme='" + chartANme + '\'' +
                ", pageBody=" + pageBody +
                '}';
    }

}
