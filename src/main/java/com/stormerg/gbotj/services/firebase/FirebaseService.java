package com.stormerg.gbotj.services.firebase;

import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public interface FirebaseService {

    Mono<Void> updateChildValues(final String path, final Map<String, Object> updates);
    Mono<Void> setValueAtPath(final String path, final Object value);
    Mono<Map<String, Object>> getValueAtPathMap(final String path);
    Mono<List<Object>> getValueAtPathList(final String path);
    Mono<String> getValueAtPathString(final String path);
    Mono<Integer> getValueAtPathInteger(final String path);
    Mono<Boolean> getValueAtPathBoolean(final String path);

}
