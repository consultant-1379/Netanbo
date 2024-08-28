package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Color {

//	@JsonProperty("color")
//	private String color;
	
	@JsonProperty("@rgb")
	private String rgb;

//	public String getColor() {
//		return color;
//	}
//
//	public void setColor(String color) {
//		this.color = color;
//	}

	public String getRgb() {
		return rgb;
	}

	public void setRgb(String rgb) {
		this.rgb = rgb;
	}

	@Override
	public String toString() {
		return "Color [rgb=" + rgb + "]";
	}

	

}
