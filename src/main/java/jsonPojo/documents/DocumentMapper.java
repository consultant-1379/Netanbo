package jsonPojo.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class DocumentMapper {

    @JsonProperty("document")
    private List<Document> document = new ArrayList<Document>();

    public List<Document> getDocuments() {
        return document;
    }

    public void setDocuments(List<Document> document) {
        this.document = document;
    }

    @Override
    public String toString() {
        return "jsonPojo.documents.DocumentMapper{" +
                "document=" + document +
                '}';
    }
}
