package com.queue.worker.runner;


import lombok.Getter;
import org.springframework.http.ResponseEntity;

import java.util.concurrent.CompletableFuture;

@Getter
public class Request implements Comparable<Request> {

    private final CompletableFuture<ResponseEntity<String>> response;
    private final String method;
    private final long timestamp;
    private final long entityId;

    public Request(CompletableFuture<ResponseEntity<String>> response, String method, long timestamp,long entityId) {
        this.response = response;
        this.method = method;
        this.timestamp = timestamp;
        this.entityId = entityId;
    }

    @Override
    public int compareTo(Request other) {
        return Long.compare(this.timestamp, other.timestamp); // Oldest requests first
    }

}