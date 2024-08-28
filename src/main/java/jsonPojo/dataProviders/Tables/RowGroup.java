package jsonPojo.dataProviders.Tables;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class RowGroup {

    @JacksonXmlProperty(localName = "type", isAttribute = true)
    private String type;

    @JacksonXmlProperty(localName = "TR")
    private Tr tr;

    @Override
    public String toString() {
        return "RowGroup{" +
                "type='" + type + '\'' +
                ", tr=" + getTr() +
                '}';
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Tr getTr() {
        return tr;
    }

    public void setTr(Tr tr) {
        this.tr = tr;
    }
}
