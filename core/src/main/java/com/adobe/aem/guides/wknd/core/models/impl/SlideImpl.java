package com.adobe.aem.guides.wknd.core.models.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource; // Import Resource if you want to adapt from it as well
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.aem.guides.wknd.core.models.Slide;

// Added Resource.class to adaptables for broader usage, common for components.
@Model(adaptables = { SlingHttpServletRequest.class, Resource.class }, adapters = {
        Slide.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, resourceType = {
                SlideImpl.RESOURCE_TYPE })
public class SlideImpl implements Slide {

    private static final Logger LOG = LoggerFactory.getLogger(SlideImpl.class); // Good practice for logging

    protected static final String RESOURCE_TYPE = "markeup/models/components/slide";

    @ValueMapValue
    private String alignment;

    @ValueMapValue
    private String title;

    @ValueMapValue
    private String description;

    @ValueMapValue
    private String buttonName;

    @ValueMapValue
    private String internalLink;

    @ValueMapValue
    private String externalLink;

    @Override
    public String getAlignment() {
        return alignment;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getButtonName() {
        return buttonName;
    }

    @Override
    public String getInternalLink() {
        // Retorna o internalLink diretamente. O campo de caminho (pathfield) do AEM
        // geralmente armazena caminhos
        // que se resolvem corretamente sem adicionar .html manualmente, especialmente
        // no ambiente de autor.
        // No ambiente de publicação, o Dispatcher lida com a reescrita de URLs. Se for
        // necessário
        // especificamente o .html, garanta que ele seja sempre desejado para todos os
        // caminhos internos.
        // Para maior robustez, considere usar um LinkRewriter ou o serviço URLProvider
        // do AEM, se necessário.
        if (internalLink != null && !internalLink.trim().isEmpty()) {
            // Se você realmente precisa que a extensão .html seja adicionada a links
            // internos, faça isso aqui.
            // Tenha cuidado, pois os caminhos internos do AEM geralmente não precisam dela
            // para serem resolvidos.
            // Exemplo: return internalLink.endsWith(".html") ? internalLink : internalLink
            // + ".html";
            return internalLink; // Most common and generally correct for AEM paths
        }
        return null;
    }

    @Override
    public String getExternalLink() {
        if (externalLink != null && !externalLink.trim().isEmpty()) {
            if (!externalLink.startsWith("http://") && !externalLink.startsWith("https://")) {
                LOG.warn("External link '{}' does not have a protocol. Prepended with http://", externalLink);
                return "http://" + externalLink;
            }
            return externalLink;
        }
        return null;
    }
}

// import org.apache.sling.api.SlingHttpServletRequest;
// import org.apache.sling.models.annotations.DefaultInjectionStrategy;
// import org.apache.sling.models.annotations.Model;
// import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

// import com.adobe.aem.guides.wknd.core.models.Slide;

// @Model(adaptables = { SlingHttpServletRequest.class }, adapters = {
// Slide.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL,
// resourceType = {SlideImpl.RESOURCE_TYPE})
// public class SlideImpl implements Slide {

// protected static final String RESOURCE_TYPE =
// "markeup/models/components/slide";

// @ValueMapValue
// private String alignment;

// @ValueMapValue
// private String title;

// @ValueMapValue
// private String description;

// @ValueMapValue
// private String buttonName;

// @ValueMapValue
// private String internalLink;

// @ValueMapValue
// private String externalLink;

// @Override
// public String getAlignment() {
// return alignment;
// }

// @Override
// public String getTitle() {
// return title;
// }

// @Override
// public String getDescription() {
// return description;
// }

// @Override
// public String getButtonName() {
// return buttonName;
// }

// @Override
// public String getInternalLink() {
// if (internalLink != null && !internalLink.isEmpty()) {
// return externalLink.concat(".html");
// }
// return null;
// }

// @Override
// public String getExternalLink() {
// if (externalLink.startsWith("http://")) {
// return internalLink;
// } else {
// return externalLink = "http://".concat(externalLink);
// }
// }
// }
