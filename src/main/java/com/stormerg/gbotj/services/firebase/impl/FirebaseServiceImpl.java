package com.stormerg.gbotj.services.firebase.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class FirebaseServiceImpl implements FirebaseService {

    private static final String SERVICE_KEY_FILE = "serviceAccountKey.json";
    private static final String VALUES_UPDATED_AT_PATH_LOG = "Values updated successfully at path: {\"path\":\"{}\",\"values\":\"{}\"}";
    private static final String VALUE_SET_AT_PATH_LOG = "Value set successfully at path: {\"path\":\"{}\",\"value\":\"{}\"}";
    private static final String VALUE_RETRIEVED_AT_PATH_LOG = "Value retrieved successfully at path: {\"path\":\"{}\",\"value\":\"{}\"}";

    private FirebaseDatabase database;

    @Autowired
    public FirebaseServiceImpl(final PropertiesManager propertiesManager) {
        try {
            // Load the service account key file as a classpath resource
            InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(SERVICE_KEY_FILE);

            // Create FirebaseOptions
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(propertiesManager.getFirebaseUrl())
                    .build();

            // Initialize FirebaseApp
            FirebaseApp.initializeApp(options);

            // Initialize FirebaseDatabase instance
            database = FirebaseDatabase.getInstance();

            log.info("FirebaseServiceImpl initialized successfully.");

        } catch (IOException e) {
            log.error("Failed to initialize FirebaseServiceImpl: {}", e.getMessage());
        }
    }

    public Mono<Void> updateChildValues(final String path, final Map<String, Object> updates) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    // Update the data in the transaction
                    for (Map.Entry<String, Object> entry : updates.entrySet()) {
                        mutableData.child(entry.getKey()).setValue(entry.getValue());
                    }
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError == null && committed) {
                        log.info(VALUES_UPDATED_AT_PATH_LOG, path, updates);
                        sink.success();
                    } else {
                        log.error("Error updating child values at path {}: {}", path, databaseError != null ? databaseError.getMessage() : "Transaction not committed");
                        sink.error(databaseError != null ? databaseError.toException() : new RuntimeException("Transaction not committed"));
                    }
                }
            });
        });
    }

    public Mono<Void> setValueAtPath(final String path, final Object value) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    mutableData.setValue(value);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                    if (databaseError == null && committed) {
                        log.info(VALUE_SET_AT_PATH_LOG, path, value);
                        sink.success();
                    } else {
                        log.error("Error setting value at path {}: {}", path, databaseError != null ? databaseError.getMessage() : "Transaction not committed");
                        sink.error(databaseError != null ? databaseError.toException() : new RuntimeException("Transaction not committed"));
                    }
                }
            });
        });
    }

    public Mono<Map<String, Object>> getValueAtPathMap(final String path) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                    logValueRetrieved(path, value);
                    sink.success(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sink.error(databaseError.toException());
                }
            });
        });
    }

    public Mono<List<Object>> getValueAtPathList(final String path) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    List<Object> value = (List<Object>) dataSnapshot.getValue();
                    logValueRetrieved(path, value);
                    sink.success(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sink.error(databaseError.toException());
                }
            });
        });
    }

    public Mono<String> getValueAtPathString(final String path) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = (String) dataSnapshot.getValue();
                    logValueRetrieved(path, value);
                    sink.success(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sink.error(databaseError.toException());
                }
            });
        });
    }

    public Mono<Integer> getValueAtPathInteger(final String path) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Integer value = dataSnapshot.getValue(Integer.class);
                    logValueRetrieved(path, value);
                    sink.success(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sink.error(databaseError.toException());
                }
            });
        });
    }

    public Mono<Boolean> getValueAtPathBoolean(final String path) {
        return Mono.create(sink -> {
            DatabaseReference reference = database.getReference(path);
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Boolean value = dataSnapshot.getValue(Boolean.class);
                    logValueRetrieved(path, value);
                    sink.success(value);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    sink.error(databaseError.toException());
                }
            });
        });
    }

    private void logValueRetrieved(String path, Object value) {
        log.info(VALUE_RETRIEVED_AT_PATH_LOG, path, value);
    }
}
