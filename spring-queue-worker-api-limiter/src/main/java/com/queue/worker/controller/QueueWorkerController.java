package com.queue.worker.controller;

import com.queue.worker.service.QueueWorkerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/queue/worker")
@RequiredArgsConstructor
public class QueueWorkerController {

    private final QueueWorkerService queueWorkerService;

    @PostMapping
    public ResponseEntity<?> queueWorker() {

        return new ResponseEntity<String>(queueWorkerService.queueWorker(), HttpStatus.OK);
    }
}
