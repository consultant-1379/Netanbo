package jsonPojo.dataProviders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProvidersMapper {

    @Override
    public String toString() {
        return "DataProvidersMapper{" +
                "dataProvider=" + dataProvider +
                '}';
    }

    @JsonProperty("dataprovider")
    private List<DataProviders> dataProvider = new ArrayList<>();

   // @JsonProperty("query")
   // private String query;

    public List<DataProviders> getDataProvider() {
        return dataProvider;
    }

    public void setDataProvider(List<DataProviders> dataProvider) {
        this.dataProvider = dataProvider;
    }


  /*  public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }*/
}