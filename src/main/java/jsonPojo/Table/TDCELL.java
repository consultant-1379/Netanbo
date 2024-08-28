package jsonPojo.Table;
import java.util.List;

public class TDCELL {

    private String bId;

    private List<CONTENT> CONTENT;


    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public List<CONTENT> getCONTENT() {
        return CONTENT;
    }

    public void setCONTENT(List<CONTENT> CONTENT) {
        this.CONTENT = CONTENT;
    }

    @Override
    public String toString() {
        return "TDCELL{" +
                "bId='" + bId + '\'' +
                ", CONTENT=" + CONTENT +
                '}';
    }
}
