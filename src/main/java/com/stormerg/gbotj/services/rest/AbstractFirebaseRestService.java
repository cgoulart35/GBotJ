package com.stormerg.gbotj.services.rest;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;

public abstract class AbstractFirebaseRestService extends AbstractRestService {

    protected final FirebaseService firebaseService;

    public AbstractFirebaseRestService(final PropertiesManager propertiesManager,
                                       final LoggingService loggingService,
                                       final FirebaseService firebaseService) {
        super(propertiesManager, loggingService);
        this.firebaseService = firebaseService;
    }
}
