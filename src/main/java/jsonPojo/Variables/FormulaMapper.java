package jsonPojo.Variables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FormulaMapper {

    @JsonProperty("variable") //you are saying json mapper , get all values for dataprovider n map to query class
    private Formula variable;


    public Formula getDpQuery() {
        return variable;
    }

    @Override
    public String toString() {
        return "FormulaMapper{" +
                "variable=" + variable +
                '}';
    }

    public void setDpQuery(Formula dpQuery) {
        this.variable = dpQuery;
    }
}
