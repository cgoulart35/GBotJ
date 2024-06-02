package com.stormerg.gbotj.services.rest.impl;

import com.stormerg.gbotj.services.properties.PropertiesManager;
import com.stormerg.gbotj.services.rest.RestService;

public abstract class AbstractRestService implements RestService {

    protected final PropertiesManager propertiesManager;

    public AbstractRestService(final PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }
}
