package com.stormerg.gbotj.services.rest.impl;

import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import com.stormerg.gbotj.services.rest.AbstractRestService;
import com.stormerg.gbotj.services.rest.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class DevelopmentRestServiceImpl extends AbstractRestService implements RestService {

    @Autowired
    public DevelopmentRestServiceImpl(final PropertiesManager propertiesManager, final LoggingService loggingService) {
        super(propertiesManager, loggingService);
    }

    @Override
    protected void setLogger(){
        LOGGER = loggingService.getLogger(DevelopmentRestServiceImpl.class);
    }
}
