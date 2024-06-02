package com.stormerg.gbotj.services.rest.impl;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class LeaderboardsRestServiceImpl extends AbstractFirebaseRestService {

    @Autowired
    public LeaderboardsRestServiceImpl(final PropertiesManager propertiesManager,
                                       final FirebaseService firebaseService) {
        super(propertiesManager, firebaseService);
    }
}
