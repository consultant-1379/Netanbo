package jsonPojo.Table;
import java.util.List;

public class TR {

    private List<TDCELL> TDCELL;


    public List<TDCELL> getTDCELL() {
        return TDCELL;
    }

    public void setTDCELL(List<TDCELL> TDCELL) {
        this.TDCELL = TDCELL;
    }

    @Override
    public String toString() {
        return "TR{" +
                "TDCELL=" + TDCELL +
                '}';
    }
}
