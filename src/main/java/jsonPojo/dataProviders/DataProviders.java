package jsonPojo.dataProviders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProviders {

    @JsonProperty("name")
    private String dataProviderName;

    @Override
    public String toString() {
        return "DataProviders{" +
                "dataProviderName='" + dataProviderName + '\'' +
                ", id='" + id + '\'' +
                ", dataSourceType='" + dataSourceType + '\'' +
                '}';
    }

    private String id;

    private String dataSourceType;

    public String getDataProviderName() {
        return dataProviderName;
    }

    public void setDataProviderName(String dataProviderName) {
        this.dataProviderName = dataProviderName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(String dataSourceType) {
        this.dataSourceType = dataSourceType;
    }
}
