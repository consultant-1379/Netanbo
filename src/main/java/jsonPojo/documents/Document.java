package jsonPojo.documents;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;

@JsonIgnoreProperties(ignoreUnknown=true)
public class Document {
    private int id;

    private String[] occurrence;

    private String cuid;

    private String name;

    private String description;

    private String keywords;

    private int folderId;


    public String[] getOccurrence() {
        return occurrence;
    }

    public void setOccurrence(String[] occurrence) {
        this.occurrence = occurrence;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCuid() {
        return cuid;
    }

    public void setCuid(String cuid) {
        this.cuid = cuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    @Override
    public String toString() {
        return "jsonPojo.documents.Document{" +
                "id=" + id +
                ", occurrence=" + Arrays.toString(occurrence) +
                ", cuid='" + cuid + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", keywords='" + keywords + '\'' +
                ", folderId=" + folderId +
                '}';
    }
}
