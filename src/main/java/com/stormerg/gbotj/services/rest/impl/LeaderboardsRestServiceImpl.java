package com.stormerg.gbotj.services.rest.impl;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import com.stormerg.gbotj.services.rest.AbstractFirebaseRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LeaderboardsRestServiceImpl extends AbstractFirebaseRestService {

    @Autowired
    public LeaderboardsRestServiceImpl(final PropertiesManager propertiesManager,
                                       final LoggingService loggingService,
                                       final FirebaseService firebaseService) {
        super(propertiesManager, loggingService, firebaseService);
    }

    @Override
    protected void setLogger(){
        LOGGER = loggingService.getLogger(LeaderboardsRestServiceImpl.class);
    }
}
