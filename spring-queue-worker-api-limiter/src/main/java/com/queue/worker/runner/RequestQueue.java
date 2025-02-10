package com.queue.worker.runner;

import org.springframework.stereotype.Component;

import java.util.concurrent.PriorityBlockingQueue;

@Component
public class RequestQueue {
    private final PriorityBlockingQueue<Request> requestQueue = new PriorityBlockingQueue<>();

    public void add(Request request) {
        requestQueue.offer(request);
    }

    public Request poll() {
        return requestQueue.poll();
    }

    public boolean isEmpty() {
        return requestQueue.isEmpty();
    }
}