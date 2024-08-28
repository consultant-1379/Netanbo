package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import java.util.ArrayList;
import java.util.List;

public class Sbody {

    @Override
    public String toString() {
        return "Sbody{" +
                "visu=" + getVisu() +
                '}';
    }

    @JacksonXmlProperty(localName = "VISU")
    @JacksonXmlElementWrapper(useWrapping = false)
    private List<Visu> visu;


    public List<Visu> getVisu() {
        return visu;
    }

    public void setVisu(List<Visu> visu) {

        if(this.visu == null) {
            this.visu = new ArrayList<>(visu.size());
        }
        this.visu.addAll(visu);
    }
}
