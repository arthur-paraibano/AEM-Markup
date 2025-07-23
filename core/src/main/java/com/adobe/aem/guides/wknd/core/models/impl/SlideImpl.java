package com.adobe.aem.guides.wknd.core.models.impl;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.adobe.aem.guides.wknd.core.models.Slide;

@Model(adaptables = { SlingHttpServletRequest.class }, // permite requests http ao model
        adapters = { Slide.class }, // proxy interface > impl
        defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL, // mapeamento entre properties jcr e atributos do
                                                                      // objeto
        resourceType = { SlideImpl.RESOURCE_TYPE } // identificacao no classloader
)
public class SlideImpl implements Slide {

    protected static final String RESOURCE_TYPE = "BootcampAemVilt/models/components/slide";

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
        return buttonName.toUpperCase();
    }

    @Override
    public String getInternalLink() {
        if (internalLink != null && !internalLink.isEmpty()) {
            return internalLink.concat(".html");
        }
        return internalLink;
    }

    @Override
    public String getExternalLink() {
        if (!externalLink.startsWith("http://") && !externalLink.startsWith("https://")) {
            return "http://".concat(externalLink);
        }
        return externalLink;
    }
}