package com.adobe.aem.guides.wknd.core.models;

import java.util.GregorianCalendar;
import java.util.List;

public interface Article {
    void setTitle(String title);

    String getTitle();

    void setDescription(String description);

    String getDescription();

    void setAuthor(String author);

    String getAuthor();

    void setDate(GregorianCalendar date);

    GregorianCalendar getDate();

    void setThumbnail(String thumbnail);

    String getThumbnail();

    void setText(String text);

    String getText();

    void setDocuments(List<Document> documents);

    List<Document> getDocuments();
}