package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Section {

    @JacksonXmlProperty(localName = "name", isAttribute = true)
    private String name;

    @JacksonXmlProperty(localName = "SBODY")
    private Sbody sbody;

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Section{" +
                "name='" + getName() + '\'' +
                ", sbody=" + getSbody() +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public Sbody getSbody() {
        return sbody;
    }

    public void setSbody(Sbody sbody) {
        this.sbody = sbody;
    }
}
