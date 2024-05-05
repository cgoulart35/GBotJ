package com.stormerg.gbotj.services.rest;

import com.stormerg.gbotj.services.properties.PropertiesManager;

public abstract class AbstractRestService implements RestService {

    protected final PropertiesManager propertiesManager;

    public AbstractRestService(final PropertiesManager propertiesManager) {
        this.propertiesManager = propertiesManager;
    }
}
