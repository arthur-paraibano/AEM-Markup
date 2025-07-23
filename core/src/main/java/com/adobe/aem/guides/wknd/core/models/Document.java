package com.adobe.aem.guides.wknd.core.models;

public class Document {

    public String name;
    public String filePath;

    public void setName(String name) {
        this.name = name;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getName() {
        return name;
    }

    public String getFilePath() {
        return filePath;
    }
}
