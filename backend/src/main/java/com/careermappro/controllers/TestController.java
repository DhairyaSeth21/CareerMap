package com.careermappro.controllers;

import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/ping")
    public Map<String, String> ping() {
        return Map.of("message", "CareerMap Backend is running!");
    }
}
