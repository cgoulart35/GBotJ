package com.stormerg.gbotj.services.firebase.impl.business;

import com.stormerg.gbotj.services.firebase.FirebaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HypeQueries {

    private final FirebaseService firebaseService;

    @Autowired
    public HypeQueries(FirebaseService firebaseService) {
        this.firebaseService = firebaseService;
    }
}
