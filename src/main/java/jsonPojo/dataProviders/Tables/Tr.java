package jsonPojo.dataProviders.Tables;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Tr {

    @JacksonXmlProperty(localName = "TDCELL")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<TdCell> tdcell;

    @Override
    public String toString() {
        return "Tr{" +
                "tdcell=" + tdcell +
                ", height='" + height + '\'' +
                '}';
    }

    @JacksonXmlProperty(localName = "height", isAttribute = true)
    private String height;


    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public List<TdCell> getTdcell() {
        return tdcell;
    }

    public void setTdcell(List<TdCell> tdcell) {
        this.tdcell = tdcell;
    }
}
