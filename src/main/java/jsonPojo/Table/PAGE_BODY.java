package jsonPojo.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

public class PAGE_BODY {

    private String bId;

    @XmlElementWrapper(name = "PAGE_BODY")
    @XmlElement(name = "VTABLE")

    private List<VTABLE> VTABLE;


    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public List<VTABLE> getVTABLE() {
        return VTABLE;
    }

    public void setVTABLE(List<VTABLE> VTABLE) {
        this.VTABLE = VTABLE;
    }

    @Override
    public String toString() {
        return "PAGE_BODY{" +
                "bId='" + bId + '\'' +
                ", VTABLE=" + VTABLE +
                '}';
    }
}
