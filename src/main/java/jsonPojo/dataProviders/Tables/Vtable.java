package jsonPojo.dataProviders.Tables;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.List;

public class Vtable {

    @JacksonXmlProperty(localName = "ROWGROUP")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<RowGroup> rowGroups;

    @JacksonXmlProperty(localName = "bId", isAttribute = true)
    private String id;

    @Override
    public String toString() {
        return "Vtables{" +
                "rowGroups=" + rowGroups +
                ", id='" + id + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RowGroup> getRowGroups() {
        return rowGroups;
    }

    public void setRowGroups(List<RowGroup> rowGroups) {
        this.rowGroups = rowGroups;
    }
}
