package com.example.demo;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TaskBuddyController {

    private final TaskbuddyRepository repository;

    public TaskBuddyController(TaskbuddyRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Taskbuddy> getTodos() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Taskbuddy addTodo(@RequestBody Taskbuddy todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "title required");
        }

        todo.setTitle(todo.getTitle().trim());

        if (todo.getCreatedAt() == null) {
            todo.setCreatedAt(System.currentTimeMillis());
        }

        return repository.save(todo);
    }

    @PutMapping("/{id}")
    public Taskbuddy updateTodo(@PathVariable Long id, @RequestBody Taskbuddy body) {
        Taskbuddy existing = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "todo not found"));

        if (body.getTitle() != null) {
            String t = body.getTitle().trim();
            if (!t.isEmpty()) existing.setTitle(t);
        }

        existing.setDone(body.isDone());

        // createdAt nicht verlieren
        if (existing.getCreatedAt() == null) {
            existing.setCreatedAt(System.currentTimeMillis());
        }

        return repository.save(existing);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "todo not found");
        }
        repository.deleteById(id);
    }
}
