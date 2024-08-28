package jsonPojo.charts;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PageBody {

    private String _bId;

    public String get_bId() {
        return _bId;
    }

    public void set_bId(String _bId) {
        this._bId = _bId;
    }

    @Override
    public String toString() {
        return "PageBody{" +
                "_bId='" + _bId + '\'' +
                '}';
    }
}
