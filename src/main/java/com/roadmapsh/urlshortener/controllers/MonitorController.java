package com.roadmapsh.urlshortener.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("monitor")
@RequestMapping("monitor")
public class MonitorController {

    @GetMapping
    public ResponseEntity<Object> status() {
        //TODO returns if the server is online and it,s details, such as: online/offline, started datetime, deployed version, time since online, resources consummed out of available.

        return ResponseEntity.ok("ONLINE");
    }

    //TODO Add services health
}
