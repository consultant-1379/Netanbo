package BOMain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParametersWrapper {
	
	@JsonProperty("parameters")
	private Parameters parameters;

	public Parameters getParameters() {
		return parameters;
	}

	public void setParameters(Parameters parameters) {
		this.parameters = parameters;
	}

	@Override
	public String toString() {
		return "ParametersWrapper [parameters=" + parameters + "]";
	}
	
}
