package com.adobe.aem.guides.wknd.core.utils;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
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

public class ArticleBuilder {

    // identica que esta class serea invocada via request http
    private SlingHttpServletRequest request;

    // Utilizado para identificar o resource correto (model) dentro do AEM
    private ResourceResolver resourceResolver;

    // Utilizado para efetuar a pesquisa e recuperar o content fragment em questão
    private QueryBuilder queryBuilder;

    public ArticleBuilder(SlingHttpServletRequest request, ResourceResolver resourceResolver) {
        this.request = request;
        this.resourceResolver = resourceResolver;
        this.queryBuilder = request.getResourceResolver().adaptTo(QueryBuilder.class);
    }

    private Map<String, String> initializeMap(String cfModel, String fragmentname) {

        final Map<String, String> parametersMap = new HashMap<String, String>();

        parametersMap.put("type", "dam:Asset");
        parametersMap.put("path", "/content/dam");
        parametersMap.put("boolproperty", "jcr:content/contentFragment");
        parametersMap.put("boolproperty.value", "true");
        parametersMap.put("1_property", "jcr:content/data/cq:model");
        parametersMap.put("2_property", "jcr:content/cq:name");
        parametersMap.put("1_property.value", cfModel);
        parametersMap.put("2_property.value", fragmentname);

        return parametersMap;
    }

    private SearchResult executeQuery(Map<String, String> parametersMap) {

        Query query = queryBuilder.createQuery(PredicateGroup.create(parametersMap),
                request.getResourceResolver().adaptTo(Session.class));

        return query.getResult();
    }

    public void buildArticle(Article article, String cfModel, String fragmentname) {
        // Define os parametrops da query
        Map<String, String> parametersMap = initializeMap(cfModel, fragmentname);

        SearchResult result = executeQuery(parametersMap);

        for (Hit hit : result.getHits()) {

            // Recupera o content fragment
            ContentFragment cfArticle = getContentFragment(hit);

            if (cfArticle != null) {
                article.setTitle(getTextValue(cfArticle, "title"));
                article.setAuthor(getTextValue(cfArticle, "author"));
                article.setDate(getDateValue(cfArticle, "date"));
                article.setThumbnail(getTextValue(cfArticle, "thumbnail"));
                article.setDescription(getTextValue(cfArticle, "description"));
                article.setText(getTextValue(cfArticle, "text"));
                article.setDocuments(getDocumentList(getListValue(cfArticle, "documents")));
            }
        }
    }

    private List<Document> getDocumentList(String[] documentsPath) {

        List<Document> documents = new LinkedList<>();
        for (String documentPath : documentsPath) {
            ContentFragment cfDocument = getContentFragmentByPath(resourceResolver, documentPath);

            Document document = new Document();
            document.setName(getTextValue(cfDocument, "fileName"));
            document.setFilePath(getTextValue(cfDocument, "file"));
            documents.add(document);
        }
        return documents;
    }

    private ContentFragment getContentFragmentByPath(ResourceResolver resourceResolver, String cfPath) {
        return resourceResolver.getResource(cfPath).adaptTo(ContentFragment.class);
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
