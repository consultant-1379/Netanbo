package jsonPojo.ReportElements;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ConditionalFormula {

	@JsonProperty("$")
	private String formula;
	
	@JsonProperty("@dataType")
	private String dataType;
	
	@JsonProperty("@qualification")
	private String qualification;
	
	@JsonProperty("@type")
	private String type;

	public String getFormula() {
		return formula;
	}

	public void setFormula(String formula) {
		this.formula = formula;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "conditionalFormula [formula=" + formula + ", dataType=" + dataType + ", qualification=" + qualification
				+ ", type=" + type + "]";
	}
	
	
}
