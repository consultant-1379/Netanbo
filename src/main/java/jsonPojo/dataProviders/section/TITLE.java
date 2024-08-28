package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TITLE {

    @JacksonXmlProperty(localName = "label", isAttribute = true)
    private String label;


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }


    @Override
    public String toString() {
        return label;
    }
}
