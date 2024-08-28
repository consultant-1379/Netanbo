package jsonPojo.dataProviders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProviderQueryMapper {

    @JsonProperty("query")
    private String query;

    @Override
    public String toString() {
        return "DataProviderQueryMapper{" +
                "query='" + getDPMapper() + '\'' +
                '}';
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }



    @JsonProperty("query")
    private List<DataProviderQuery> dpquery = new ArrayList<DataProviderQuery>();

    public List<DataProviderQuery> getDPMapper() {
        return dpquery;
    }

    public void setDocuments(List<DataProviderQuery> getDPMapper) {
        this.dpquery = dpquery;
    }

}
