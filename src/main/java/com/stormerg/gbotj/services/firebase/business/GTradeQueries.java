package com.stormerg.gbotj.services.firebase.business;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GTradeQueries {

    private final FirebaseService firebaseService;

    @Autowired
    public GTradeQueries(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
}
