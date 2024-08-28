package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Visu {

    @JacksonXmlProperty(localName = "XY_CHART")
    private XyChart xyChart;

    @JacksonXmlProperty(localName = "bId", isAttribute = true)
    private String bId;

    public XyChart getXyChart() {
        return xyChart;
    }

    @Override
    public String toString() {
        return "Visu{" +
                "xyChart=" + xyChart +
                ", bId='" + bId + '\'' +
                '}';
    }

    public void setXyChart(XyChart xyChart) {
        this.xyChart = xyChart;
    }

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }
}
