package jsonPojo.Table;
import java.util.List;

public class ROWGROUP {

    private String type;

    private List<TR> TR;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TR> getTR() {
        return TR;
    }

    public void setTR(List<TR> TR) {
        this.TR = TR;
    }

    @Override
    public String toString() {
        return "ROWGROUP{" +
                "type='" + type + '\'' +
                ", TR=" + TR +
                '}';
    }
}
