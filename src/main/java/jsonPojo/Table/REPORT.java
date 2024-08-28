package jsonPojo.Table;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "REPORT")
@XmlType(propOrder = { "PAGE_HEADER", "PAGE_BODY" })
public class REPORT {

    private String rId;

    private String name;

    //@XmlElementWrapper(name = "PAGE_BODY")
    @XmlElement(name = "PAGE_BODY")
    private List<PAGE_BODY> PAGE_BODY;


    public String getrId() {
        return rId;
    }

    public void setrId(String rId) {
        this.rId = rId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlElement(name = "PAGE_BODY")
    public List<PAGE_BODY> getPAGE_BODY() {
        return PAGE_BODY;
    }

    public void setPAGE_BODY(List<PAGE_BODY> PAGE_BODY) {
        this.PAGE_BODY = PAGE_BODY;
    }

    @Override
    public String toString() {
        return "REPORT{" +
                "rId='" + rId + '\'' +
                ", name='" + name + '\'' +
                ", PAGE_BODY=" + PAGE_BODY +
                '}';
    }
}
