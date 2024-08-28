package jsonPojo.dataProviders;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class DataProviderQuery {
  
       // @JsonProperty("query")
        private String query;

        @Override
        public String toString() {
            return "DataProvider{" +
                    "query='" + query + '\'' +
                    ", id='" + id + '\'' +
                    ", dataSourceType='" + dataSourceType + '\'' +
                    '}';
        }

        private String id;

        private String dataSourceType;
        private String name;

        public String getDataProviderName() {
            return query;
        }

        public void setDataProviderName(String dataProviderName) {
            this.query = dataProviderName;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}


