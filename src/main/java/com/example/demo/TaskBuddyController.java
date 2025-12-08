package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
@CrossOrigin(origins = "*")
public class TaskBuddyController {

    private final TaskbuddyRepository repo;

    public TaskBuddyController(TaskbuddyRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Taskbuddy> getTodos() {
        return repo.findAll();
    }

    @PostMapping
    public Taskbuddy addTodo(@RequestBody Taskbuddy todo) {
        return repo.save(todo);
    }
}
