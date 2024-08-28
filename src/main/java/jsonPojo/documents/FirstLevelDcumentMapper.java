package jsonPojo.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class FirstLevelDcumentMapper {

    @JsonProperty("documents")
    private DocumentMapper mapper;

    @Override
    public String toString() {
        return "FirstLevelDcumentMapper{" +
                "mapper=" + getMapper() +
                '}';
    }

    public DocumentMapper getMapper() {
        return mapper;
    }

    public void setMapper(DocumentMapper mapper) {
        this.mapper = mapper;
    }
}
