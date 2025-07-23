package com.adobe.aem.guides.wknd.core.utils;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.ResourceResolver;

import com.adobe.aem.guides.wknd.core.models.Article;
import com.adobe.aem.guides.wknd.core.models.Document;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.day.cq.search.PredicateGroup;
import com.day.cq.search.Query;
import com.day.cq.search.QueryBuilder;
import com.day.cq.search.result.Hit;
import com.day.cq.search.result.SearchResult;

/*
 * Recebe um ContentFragment faz a conversão para um objeto Article
 */
public class ArticleBuilder {

    // Identifica que a classe será invocada via requests HTTP
    private SlingHttpServletRequest request;
    // Identifica o model dentro do AEM
    private ResourceResolver resolver;
    // Responsável pelas queries por encontrar o Content Fragment correto
    private QueryBuilder queryBuilder;

    public ArticleBuilder(SlingHttpServletRequest request, ResourceResolver resolver) {
        this.request = request;
        this.resolver = resolver;
        this.queryBuilder = request.getResourceResolver().adaptTo(QueryBuilder.class);
    }

    private Map<String, String> initializeMap(String cfModel, String fragmentName) {
        final Map<String, String> paramsMap = new HashMap<String, String>();
        paramsMap.put("type", "dam:Asset");
        paramsMap.put("path", "/content/dam");
        paramsMap.put("boolproperty", "jcr:content/contentFragment");
        paramsMap.put("boolproperty.value", "true");
        paramsMap.put("1_property", "jcr:content/data/cq:model");
        paramsMap.put("2_property", "jcr:content/cq:name");
        paramsMap.put("1_property.value", cfModel);
        paramsMap.put("2_property.value", fragmentName);
        return paramsMap;
    }

    private SearchResult executeQuery(Map<String, String> paramsMap) {
        Query query = queryBuilder.createQuery(PredicateGroup.create(paramsMap),
                request.getResourceResolver().adaptTo(Session.class));
        return query.getResult();
    }

    public void buildArticle(Article article, String cfModel, String fragmentName) {
        Map<String, String> paramsMap = initializeMap(cfModel, fragmentName); // params da query
        SearchResult result = executeQuery(paramsMap); // executa a query
        for (Hit hit : result.getHits()) { // iterar o result
            ContentFragment cfArticle = getContentFragment(hit);
            if (cfArticle != null) {
                article.setTitle(getTextValue(cfArticle, "title"));
                article.setAuthor(getTextValue(cfArticle, "author"));
                article.setTitle(getTextValue(cfArticle, "title"));
                article.setDate(getDateValue(cfArticle, "date"));
                article.setDescription(getTextValue(cfArticle, "description"));
                article.setThumbnail(getTextValue(cfArticle, "thumbnail"));
                article.setText(getTextValue(cfArticle, "text"));
                article.setDocuments(getDocumentList(getListValue(cfArticle, "documents")));
            }
        }
    }

    private List<Document> getDocumentList(String[] filepaths) {
        List<Document> documents = new ArrayList<>();
        for (String filepath : filepaths) {
            ContentFragment cfDocument = getContentFragmentByFilepath(resolver, filepath);
            Document document = new Document();
            document.setFile(getTextValue(cfDocument, "file"));
            document.setName(getTextValue(cfDocument, "fileName"));
            documents.add(document);
        }
        return documents;
    }

    private ContentFragment getContentFragmentByFilepath(ResourceResolver resolver, String cfPath) {
        return Objects.requireNonNull(resolver.getResource(cfPath)).adaptTo(ContentFragment.class);
    }

    private String[] getListValue(ContentFragment cf, String attribute) {
        return (String[]) Objects.requireNonNull(cf.getElement(attribute).getValue().getValue());
    }

    private GregorianCalendar getDateValue(ContentFragment cf, String attribute) {
        return (GregorianCalendar) Objects.requireNonNull(cf.getElement(attribute).getValue().getValue());
    }

    private String getTextValue(ContentFragment cf, String attribute) {
        return Objects.requireNonNull(cf.getElement(attribute).getValue().getValue()).toString();
    }

    private ContentFragment getContentFragment(Hit hit) {
        ContentFragment cf = null;
        try {
            cf = hit.getResource().adaptTo(ContentFragment.class);
        } catch (RepositoryException e) {
            e.printStackTrace();
        }
        return cf;
    }

}