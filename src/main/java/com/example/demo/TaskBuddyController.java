package com.example.demo;

import org.springframework.web.bind.annotation.*;

import java.util.List;


    @RestController
    @RequestMapping("/api/todos")
    @CrossOrigin
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
        public Taskbuddy addTodo(@RequestBody Taskbuddy todo) {
            return repository.save(todo);
        }

       @DeleteMapping("/{id}")
        public void deleteTodo(@PathVariable Long id) {
            repository.deleteById(id);
        }
    }


