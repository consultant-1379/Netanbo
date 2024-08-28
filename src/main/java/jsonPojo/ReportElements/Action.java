package jsonPojo.ReportElements;
 
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Action {
	
	@JsonProperty("data")
	private Data data;
	
	public Data getData() {
		return data;
	}
	public void setData(Data data) {
		this.data = data;
	}
///*	
    @JsonProperty("style")
	private Style style;
	
	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}
 
	@Override
	public String toString() {
		return "Action [data=" + data + ", style=" + style + "]";
	}

//*/	
///*	
	/*
	 * @Override public String toString() { System.out.println("data " +
	 * data.toString()); return "Action [data=" + data + "]"; }
	 */
//*/
}
