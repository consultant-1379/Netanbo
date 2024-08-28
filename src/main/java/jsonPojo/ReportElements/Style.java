package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Style {

	/*@JsonProperty("style")
	private Style style;
	
	public Style getStyle() {
		return style;
	}

	public void setStyle(Style style) {
		this.style = style;
	}*/
 
	@JsonProperty(value = "background", required= false)
	private BackGround background;
	
	@JsonProperty("font")
	private Font font;

	public BackGround getBackground() {
		return background;
	}

	public void setBackground(BackGround background) {
		this.background = background;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}
	@Override
	public String toString() {
		return "Style [background=" + background + ", font=" + font + "]";
	}

}
