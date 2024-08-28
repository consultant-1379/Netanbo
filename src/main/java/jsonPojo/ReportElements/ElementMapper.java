package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ElementMapper {
	
	@JsonProperty("element")
	private ElementData element;

	public ElementData getElement() {
		return element;
	}

	public void setElement(ElementData element) {
		this.element = element;
	}

	@Override
	public String toString() {
		return "ElementMapper [element=" + element + "]";
	}
	
}

