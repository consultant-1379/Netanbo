package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Font {
	
	//@JsonProperty("$")
	//private String font;
	
	@JsonProperty("@rgb")
	private String rgb;

	/*public String getFont() {
		return font;
	}

	public void setFont(String font) {
		this.font = font;
	}*/

	@Override
	public String toString() {
		return "Font, [rgb=" + rgb + "]";
	}

	public String getRgb() {
		return rgb;
	}

	public void setRgb(String rgb) {
		this.rgb = rgb;
	}

/*	@Override
	public String toString() {
		return "Font [rgb=" + rgb + "]";
	}
*/	

}
