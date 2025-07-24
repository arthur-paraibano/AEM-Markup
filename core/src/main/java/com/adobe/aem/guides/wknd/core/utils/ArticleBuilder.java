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

/**
 * Classe responsável por construir ou recuperar os dados de um "Article"
 * a partir de um request HTTP, usando AEM APIs (ResourceResolver e
 * QueryBuilder).
 */
public class ArticleBuilder {

    // Identifica que esta classe será invocada via request HTTP
    private SlingHttpServletRequest request;

    // Utilizado para identificar o resource correto (model) dentro do AEM
    private ResourceResolver resourceResolver;

    // Utilizado para efetuar a pesquisa e recuperar o content fragment em questão
    private QueryBuilder queryBuilder;

    /**
     * Construtor recebe o request HTTP e o ResourceResolver para operações AEM.
     *
     * @param request          SlingHttpServletRequest recebido pela servlet ou
     *                         modelo Sling
     * @param resourceResolver ResourceResolver já obtido com permissões apropriadas
     */
    public ArticleBuilder(SlingHttpServletRequest request, ResourceResolver resourceResolver) {
        this.request = request;
        this.resourceResolver = resourceResolver;
        // Obtém o QueryBuilder adaptando o ResourceResolver do request (ou pode usar o
        // passado via parâmetro)
        // Dependendo do contexto, você pode usar:
        // this.queryBuilder = resourceResolver.adaptTo(QueryBuilder.class);
        // Ou:
        this.queryBuilder = request.getResourceResolver().adaptTo(QueryBuilder.class);
    }

    private Map<String, String> initializeMap(String cfModel, String fragmentName) {
        final Map<String, String> parameterMap = new HashMap<String, String>();

        parameterMap.put("type", "dam:Asset");
        parameterMap.put("path", "/content/dam");
        // propriedade booleana: verificar se é Content Fragment
        parameterMap.put("boolproperty", "jcr:content/contentFragment");
        parameterMap.put("boolproperty.value", "true");
        // primeiro predicate de propriedade: modelo do Content Fragment
        parameterMap.put("1_property", "jcr:content/data/cq:model");
        parameterMap.put("2_property", "jcr:content/cq:name");
        parameterMap.put("1_property.value", cfModel);
        parameterMap.put("2_property.value", fragmentName);

        return parameterMap;
    }

    private SearchResult executeQuery(Map<String, String> parametersMap) {
        Query query = queryBuilder.createQuery(
                PredicateGroup.create(parametersMap),
                request.getResourceResolver().adaptTo(Session.class));
        return query.getResult();
    }

    public void buildArticle(Article article, String cfModel, String fragmentName) {

        Map<String, String> parameterMap = initializeMap(cfModel, fragmentName);

        SearchResult result = executeQuery(parameterMap);

        for (Hit hit : result.getHits()) {
            ContentFragment cfArticle = getContentFragment(hit);
            if (cfArticle != null) {
                article.setTitle(getTextValue(cfArticle, "title"));
                article.setAuthor(getTextValue(cfArticle, "author"));
                article.setDate(getDateValue(cfArticle, "date"));
                article.setDescription(getTextValue(cfArticle, "description"));
                article.setThumbnail(getTextValue(cfArticle, "thumbnail"));
                article.setText(getTextValue(cfArticle, "text"));

                article.setDocuments(
                        getDocumentsList(getListValue(cfArticle, "documents")));
            }
        }

    }

    private List<Document> getDocumentsList(String[] documentsPath) {
        List<Document> documents = new LinkedList<>();
        for (String documentPath : documentsPath) {
            ContentFragment cfDocument = getContentFragmentByPath(resourceResolver, documentPath);
            Document document = new Document();
            document.setName(getTextValue(cfDocument, "fileName"));
            document.setFile(getTextValue(cfDocument, "file"));

            documents.add(document);
        }
        return documents;
    }

    private ContentFragment getContentFragmentByPath(ResourceResolver resourceResolver, String cfPath) {
        return resourceResolver.getResource(cfPath).adaptTo(ContentFragment.class);

    }

    private String[] getListValue(ContentFragment cf, String atribute) {
        return (String[]) Objects.requireNonNull(
                cf.getElement(atribute).getValue().getValue());
    }

    private GregorianCalendar getDateValue(ContentFragment cf, String atribute) {
        return (GregorianCalendar) Objects.requireNonNull(
                cf.getElement(atribute).getValue().getValue());
    }

    private String getTextValue(ContentFragment cf, String atribute) {
        return Objects.requireNonNull(
                cf.getElement(atribute).getValue().getValue().toString());
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