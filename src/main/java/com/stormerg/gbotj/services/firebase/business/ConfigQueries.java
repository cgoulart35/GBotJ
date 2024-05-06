package com.stormerg.gbotj.services.firebase.business;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ConfigQueries {

    private final FirebaseService firebaseService;

    @Autowired
    public ConfigQueries(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }

    public Mono<Boolean> getFeatureToggleValue(final String serverId, final String featureName) {
        return firebaseService.getValueAtPathBoolean("/servers/" + serverId + "/" + featureName);
    }
}
