package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;
@CrossOrigin(origins = "*")
@RestController
public class TaskBuddy2EntryController {

    @GetMapping("/TaskBuddy")
    public String TaskBuddy() { return "M1, M2, M3";
        //public List<TodoEntry> getTodoEntries() {
        //return List.of(new TodoEntry("M1"), new TodoEntry("M2"), new TodoEntry("M3"));
    }

}