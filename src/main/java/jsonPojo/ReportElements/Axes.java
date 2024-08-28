package jsonPojo.ReportElements;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Axes {
	
	@JsonProperty("axis")
	private ArrayList<Axis> axis = new ArrayList<>();

	public ArrayList<Axis> getAxis() {
		return axis;
	}

	public void setAxis(ArrayList<Axis> axis) {
		this.axis = axis;
	}

	@Override
	public String toString() {
		return "Axes [axis=" + axis + "]";
	}
	
	
}
