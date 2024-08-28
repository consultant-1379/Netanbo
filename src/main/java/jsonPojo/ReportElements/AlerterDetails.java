package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AlerterDetails {
	
	@JsonProperty("id")
	private Integer id;
	
	@JsonProperty("name")
	private String name;
	
	@JsonProperty("description")
	private String description;
	
	@JsonProperty("rule")
	ArrayList<Rule> rule = new ArrayList<>();
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<Rule> getRule() {
		return rule;
	}

	public void setRule(ArrayList<Rule> rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		return "AlerterDetails [id=" + id + ", name=" + name + ", rule=" + rule + "]";
	}
	
}
