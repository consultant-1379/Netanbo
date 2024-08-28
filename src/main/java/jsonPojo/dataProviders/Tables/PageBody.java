package jsonPojo.dataProviders.Tables;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import jsonPojo.dataProviders.section.Section;

public class PageBody {

    private boolean isTaable;

    private boolean isChart;

    @JacksonXmlProperty(localName = "VTABLE")
    private Vtable vtables;

    @JacksonXmlProperty(localName = "SECTION")
    private Section section;

    @JacksonXmlProperty(localName = "bId", isAttribute = true)
    private String id;


    @Override
    public String toString() {
        return "PageBody{" +
                "vtables=" + vtables +
                ", section=" + section +
                ", id='" + id + '\'' +
                '}';
    }

    public Vtable getVtables() {
        return vtables;
    }

    public void setVtables(Vtable vtables) {
        isTaable = true;
        this.vtables = vtables;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        isChart = true;
        this.section = section;
    }

    public boolean isTaable() {
        return isTaable;
    }

    public boolean isChart() {
        return isChart;
    }
}
