package com.stormerg.gbotj.services.firebase.impl;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.*;
import com.stormerg.gbotj.services.firebase.FirebaseService;
import com.stormerg.gbotj.services.logging.LoggingService;
import com.stormerg.gbotj.services.properties.PropertiesManager;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public class FirebaseServiceImpl implements FirebaseService {

    private static final String SERVICE_ACCOUNT_KEY_PATH = "src/main/resources/serviceAccountKey.json";
    private static final String VALUES_UPDATED_AT_PATH_LOG = "Values updated successfully at path: {\"path\":\"{}\",\"values\":\"{}\"}";
    private static final String VALUE_SET_AT_PATH_LOG = "Value set successfully at path: {\"path\":\"{}\",\"value\":\"{}\"}";
    private static final String VALUE_RETRIEVED_AT_PATH_LOG = "Value retrieved successfully at path: {\"path\":\"{}\",\"value\":\"{}\"}";

    private final Logger LOGGER;

    private FirebaseDatabase database;

    @Autowired
    public FirebaseServiceImpl(final PropertiesManager propertiesManager, final LoggingService loggingService) {
        LOGGER = loggingService.getLogger(FirebaseServiceImpl.class);

        try {
            // Create FirebaseOptions
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(new FileInputStream(SERVICE_ACCOUNT_KEY_PATH)))
                    .setDatabaseUrl(propertiesManager.getFirebaseUrl())
                    .build();

            // Initialize FirebaseApp
            FirebaseApp.initializeApp(options);

            // Initialize FirebaseDatabase instance
            database = FirebaseDatabase.getInstance();

            LOGGER.info("FirebaseServiceImpl initialized successfully.");

        } catch (IOException e) {
            LOGGER.error("Failed to initialize FirebaseServiceImpl: {}", e.getMessage());
        }
    }

    public CompletableFuture<Void> updateChildValues(final String path, final Map<String, Object> updates) {
        CompletableFuture<Void> future = new CompletableFuture<>();
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
                    LOGGER.info(VALUES_UPDATED_AT_PATH_LOG, path, updates);
                    future.complete(null);
                } else {
                    LOGGER.error("Error updating child values at path {}: {}", path, databaseError != null ? databaseError.getMessage() : "Transaction not committed");
                    future.completeExceptionally(databaseError != null ? databaseError.toException() : new RuntimeException("Transaction not committed"));
                }
            }
        });
        return future;
    }

    public CompletableFuture<Void> setValueAtPath(final String path, final Object value) {
        CompletableFuture<Void> future = new CompletableFuture<>();
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
                    LOGGER.info(VALUE_SET_AT_PATH_LOG, path, value);
                    future.complete(null);
                } else {
                    LOGGER.error("Error setting value at path {}: {}", path, databaseError != null ? databaseError.getMessage() : "Transaction not committed");
                    future.completeExceptionally(databaseError != null ? databaseError.toException() : new RuntimeException("Transaction not committed"));
                }
            }
        });
        return future;
    }

    public CompletableFuture<Map<String, Object>> getValueAtPathMap(final String path) {
        CompletableFuture<Map<String, Object>> future = new CompletableFuture<>();
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Map<String, Object> value = (Map<String, Object>) dataSnapshot.getValue();
                logValueRetrieved(path, value);
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<List<Object>> getValueAtPathList(final String path) {
        CompletableFuture<List<Object>> future = new CompletableFuture<>();
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Object> value = (List<Object>) dataSnapshot.getValue();
                logValueRetrieved(path, value);
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<String> getValueAtPathString(final String path) {
        CompletableFuture<String> future = new CompletableFuture<>();
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String value = (String) dataSnapshot.getValue();
                logValueRetrieved(path, value);
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Integer> getValueAtPathInteger(final String path) {
        CompletableFuture<Integer> future = new CompletableFuture<>();
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Integer value = dataSnapshot.getValue(Integer.class);
                logValueRetrieved(path, value);
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    public CompletableFuture<Boolean> getValueAtPathBoolean(final String path) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        DatabaseReference reference = database.getReference(path);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Boolean value = dataSnapshot.getValue(Boolean.class);
                logValueRetrieved(path, value);
                future.complete(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                future.completeExceptionally(databaseError.toException());
            }
        });
        return future;
    }

    private void logValueRetrieved(String path, Object value) {
        LOGGER.info(VALUE_RETRIEVED_AT_PATH_LOG, path, value);
    }
}
