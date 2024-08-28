package jsonPojo.Table;
import javax.xml.bind.annotation.XmlElement;
import java.util.List;

public class VTABLE {

    private String bId;

    private String name;

    @XmlElement(name = "ROWGROUP")
    private List<ROWGROUP> ROWGROUP;


    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<ROWGROUP> getROWGROUP() {
        return ROWGROUP;
    }

    public void setROWGROUP(List<ROWGROUP> ROWGROUP) {
        this.ROWGROUP = ROWGROUP;
    }

    @Override
    public String toString() {
        return "VTABLE{" +
                "bId='" + bId + '\'' +
                ", name='" + name + '\'' +
                ", ROWGROUP=" + ROWGROUP +
                '}';
    }
}
