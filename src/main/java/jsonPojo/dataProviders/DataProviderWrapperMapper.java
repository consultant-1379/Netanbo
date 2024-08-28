package jsonPojo.dataProviders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProviderWrapperMapper {

    @JsonProperty("dataproviders")
    private
    DataProvidersMapper dataProvidersMapper;


    public DataProvidersMapper getDataProvidersMapper() {
        return dataProvidersMapper;
    }

    public void setDataProvidersMapper(DataProvidersMapper dataProvidersMapper) {
        this.dataProvidersMapper = dataProvidersMapper;
    }
}
