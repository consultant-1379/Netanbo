package jsonPojo.ReportElements;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementFormula {

	@JsonProperty("$")
	private String kpiName;
	
	@JsonProperty("@dataType")
	private String dataType;
	
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@JsonProperty("@type")
	private String type;

	
	public void setKpiName(String kpiName) {
		Pattern pattern = Pattern.compile("(\\[[^\\[]*\\]\\.){1,}" + "(\\[[^\\[]*\\])");
		Matcher m = pattern.matcher(kpiName);
		while(m.find()) {
			kpiName = m.group(2);
		}
		this.kpiName = kpiName.replaceFirst("\\[", "").replaceAll("\\]$", "")
				.replaceAll("=", "");
	}
	public String getKpiName() {
        return kpiName.replaceAll("\\\\","");
    }

	@Override
	public String toString() {
		return "ElementFormula [kpiName=" + kpiName + "]";
	}
	
	
}
