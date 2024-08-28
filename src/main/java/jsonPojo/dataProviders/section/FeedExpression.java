package jsonPojo.dataProviders.section;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import static jsonPojo.dataProviders.Tables.Report.prev;

public class FeedExpression {

    @JacksonXmlProperty(localName = "CONTENT")
    private String content;
    

    public String getContent() {

      //  content = content.substring(1, content.length());
    	prev = "";
        return content;// = content.substring(1, content.length());
    }

    @Override
    public String toString() {
        return content;
    }

    //testing
    public void setContent(String content) {
    	
    	if(! prev.isEmpty())
    		content = content + "@" +  prev ;
    	prev = content;
    	
        this.content = content;
    }
}
