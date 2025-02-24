package com.stormerg.gbotj.services.rest.impl;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;

public abstract class AbstractFirebaseRestService extends AbstractRestService {

    protected final FirebaseService firebaseService;

    public AbstractFirebaseRestService(final PropertiesManager propertiesManager,
                                       final FirebaseService firebaseService) {
        super(propertiesManager);
        this.firebaseService = firebaseService;
    }
}
