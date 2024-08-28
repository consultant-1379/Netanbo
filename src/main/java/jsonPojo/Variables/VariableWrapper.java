package jsonPojo.Variables;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VariableWrapper {

    @JsonProperty("variables")
    private VariableMapper mapper;

    @Override
    public String toString() {
        return "VariableWrapper{" +
                "mapper=" + getMapper() +
                '}';
    }

    public VariableMapper getMapper() {
        return mapper;
    }

    public void setMapper(VariableMapper mapper) {
        this.mapper = mapper;
    }
}
