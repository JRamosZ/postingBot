package com.smartecmx.postingbot.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    
    @GetMapping("/home")
    public ResponseEntity hellothere() {
        return new ResponseEntity<>("Hello, World!", org.springframework.http.HttpStatus.OK);
    }
}
