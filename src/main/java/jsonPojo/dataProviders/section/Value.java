package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class Value {

    @JacksonXmlProperty(localName = "FEED_EXPR")
    private FeedExpression feedExpression;


    @Override
    public String toString() {
        return "Category{" +
                "feedExpression=" + feedExpression +
                '}';
    }

    public FeedExpression getFeedExpression() {
        return feedExpression;
    }

    public void setFeedExpression(FeedExpression feedExpression) {
        this.feedExpression = feedExpression;
    }

}
