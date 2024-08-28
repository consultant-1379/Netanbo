package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class XyChart {

    @Override
    public String toString() {
        return "XyChart{" +
                "feeds=" + getFeeds() +
                '}';
    }

    @JacksonXmlProperty(localName = "FEEDS")
    private Feeds feeds;

    @JacksonXmlProperty(localName = "VALUE_AXIS")
    private ValueAxis valueAxis;



    public Feeds getFeeds() {
        return feeds;
    }

    public void setFeeds(Feeds feeds) {
        this.feeds = feeds;
    }

    public ValueAxis getValueAxis() {
        return valueAxis;
    }

    public void setValueAxis(ValueAxis valueAxis) {
        this.valueAxis = valueAxis;
    }
}
