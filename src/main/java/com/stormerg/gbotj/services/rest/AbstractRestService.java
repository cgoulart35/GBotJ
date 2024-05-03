package com.stormerg.gbotj.services.rest;

import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import org.slf4j.Logger;

public abstract class AbstractRestService implements RestService {

    protected final PropertiesManager propertiesManager;
    protected final LoggingService loggingService;

    protected Logger LOGGER;

    public AbstractRestService(final PropertiesManager propertiesManager, final LoggingService loggingService) {
        this.propertiesManager = propertiesManager;
        this.loggingService = loggingService;

        this.setLogger();
    }

    protected abstract void setLogger();
}
