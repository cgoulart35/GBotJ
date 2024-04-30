package com.stormerg.gbotj.services.firebase;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface FirebaseService {

    CompletableFuture<Void> updateChildValues(final String path, final Map<String, Object> updates);
    CompletableFuture<Void> setValueAtPath(final String path, final Object value);
    CompletableFuture<Map<String, Object>> getValueAtPathMap(final String path);
    CompletableFuture<List<Object>> getValueAtPathList(final String path);
    CompletableFuture<String> getValueAtPathString(final String path);
    CompletableFuture<Integer> getValueAtPathInteger(final String path);
    CompletableFuture<Boolean> getValueAtPathBoolean(final String path);

}
