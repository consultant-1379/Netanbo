package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BackGround {
	
	@JsonProperty(value = "color", required = false)
	@JsonInclude(Include.NON_NULL)
	private Color color;
	
	@JsonProperty("@width")
	private String width;

	public String getWidth() {
		return width;
	}
	
	public void setWidth(String width) {
		this.width = width;
	}
	
	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	
	@Override
	public String toString() {
		return "BackGround [color=" + color + "]";
	}
	
//	@JsonProperty("@operator")
//	private String operator;
	
}
