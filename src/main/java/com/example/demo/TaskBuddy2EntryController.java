package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskBuddy2EntryController {

    @GetMapping("/TaskBuddy")
    public String taskBuddy() {
        return "Online ✅";
    }
}
