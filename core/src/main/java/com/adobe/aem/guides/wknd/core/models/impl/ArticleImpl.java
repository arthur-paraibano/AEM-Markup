package com.adobe.aem.guides.wknd.core.models.impl;

import java.util.GregorianCalendar;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.RequestAttribute;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.apache.sling.models.annotations.injectorspecific.SlingObject;

import com.adobe.aem.guides.wknd.core.models.Article;
import com.adobe.aem.guides.wknd.core.models.Document;
// import com.adobe.aem.guides.wknd.core.utils.ArticleBuilder;
import com.adobe.aem.guides.wknd.core.utils.ArticleBuilder;

@Model(adaptables = { SlingHttpServletRequest.class }, adapters = {
        Article.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = ArticleImpl.RESOURCE_TYPE)
public class ArticleImpl implements Article {

    protected static final String RESOURCE_TYPE = "BootcampAemVilt/models/components/article";

    private String title;
    private String description;
    private String author;
    private GregorianCalendar date;
    private String thumbnail;
    private String text;
    private List<Document> documents;

    @Self
    private SlingHttpServletRequest request;
    @SlingObject
    private ResourceResolver resolver;
    @RequestAttribute
    private String fragmentName;

    @PostConstruct
    protected void init() {
        ArticleBuilder builder = new ArticleBuilder(request, resolver);
        builder.buildArticle(this, "/conf/BootcampAemVilt/settings/dam/cfm/models/artigo", fragmentName);
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public String getAuthor() {
        return author;
    }

    @Override
    public void setDate(GregorianCalendar date) {
        this.date = date;
    }

    @Override
    public GregorianCalendar getDate() {
        return date;
    }

    @Override
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    @Override
    public String getThumbnail() {
        return thumbnail;
    }

    @Override
    public void setText(String text) {
        this.text = text;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    @Override
    public List<Document> getDocuments() {
        return documents;
    }
}