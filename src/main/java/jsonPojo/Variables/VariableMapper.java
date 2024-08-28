package jsonPojo.Variables;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class VariableMapper {

    @JsonProperty("variable")
    private List<Variables> variable = new ArrayList<Variables>();

    public List<Variables> getVariables() {
        return variable;
    }

    public void setVariables(List<Variables> variable) {
        this.variable = variable;
    }

    @Override
    public String toString() {
        return "jsonPojo.Variables.VariableMapper{" +
                "variable=" + variable +
                '}';
    }
}