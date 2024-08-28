package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class ValueAxis {

    @JacksonXmlProperty(localName = "TITLE")
    private TITLE title;


    public TITLE getTitle() {
        return title;
    }

    public void setTitle(TITLE title) {
        this.title = title;
    }
}
