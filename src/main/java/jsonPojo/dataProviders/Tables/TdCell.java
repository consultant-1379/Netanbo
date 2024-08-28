package jsonPojo.dataProviders.Tables;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class TdCell {

    @JacksonXmlProperty(localName = "bId", isAttribute = true)
    private String bId;

    @JacksonXmlProperty(localName = "styleId", isAttribute = true)
    private String styleId;

    @Override
    public String toString() {
        return "TdCell{" +
                "bId='" + bId + '\'' +
                ", styleId='" + styleId + '\'' +
                ", content='" + content + '\'' +
                '}';
    }

    @JacksonXmlProperty(localName = "CONTENT")
    private String content;

    public String getbId() {
        return bId;
    }

    public void setbId(String bId) {
        this.bId = bId;
    }

    public String getStyleId() {
        return styleId;
    }

    public void setStyleId(String styleId) {
        this.styleId = styleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
