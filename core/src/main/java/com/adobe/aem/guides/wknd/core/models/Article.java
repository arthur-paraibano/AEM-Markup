package com.adobe.aem.guides.wknd.core.models;

import java.util.GregorianCalendar;
import java.util.List;

public interface Article {

    void setTitle(String title);

    void setAuthor(String author);

    void setDate(GregorianCalendar date);

    void setThumbnail(String thumbnail);

    void setDescription(String description);

    void setText(String text);

    void setDocuments(List<Document> documents);

    String getTitle();

    String getAuthor();

    GregorianCalendar getDate();

    String getThumbnail();

    String getDescription();

    String getText();

    List<Document> getDocuments();
}
