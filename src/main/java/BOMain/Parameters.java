package BOMain;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Parameters {
	
	@JsonProperty("parameter")
	private ArrayList<Parameter> parameter = new ArrayList<Parameter>();

	public ArrayList<Parameter> getParameter() {
		return parameter;
	}

	public void setParameter(ArrayList<Parameter> parameter) {
		this.parameter = parameter;
	}

	@Override
	public String toString() {
		return "Parameters [parameter=" + parameter + "]";
	}
	
	
}
