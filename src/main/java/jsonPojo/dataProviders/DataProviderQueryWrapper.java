
package jsonPojo.dataProviders;

        import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
        import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProviderQueryWrapper {

    @JsonProperty("dataprovider")
    private
    DataProviderQueryMapper dataProvidersQueryMapper;


    public DataProviderQueryMapper getDataProvidersQueryMapper() {
        return dataProvidersQueryMapper;
    }

    public void setDataProvidersQueryMapper(DataProviderQueryMapper dataProvidersMapper) {
        this.dataProvidersQueryMapper = dataProvidersQueryMapper;
    }
}
