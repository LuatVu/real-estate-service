package com.realestate.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.services.ElasticSearchService;

@RestController
@RequestMapping("/api/sync")
public class SyncController {
    @Autowired
    private ElasticSearchService elasticSearchService;

    @PostMapping("/posts")
    public ResponseEntity<String> syncToElastic() {
        elasticSearchService.syncToElasticSearch();
        return ResponseEntity.ok("Sync completed");
    }
}
