package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "taskbuddy")
public class Taskbuddy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private boolean done;

    // timestamp (ms) für UI-Datum
    @Column(nullable = false)
    private Long createdAt;

    public Taskbuddy() {}

    public Taskbuddy(String title, boolean done) {
        this.title = title;
        this.done = done;
        this.createdAt = System.currentTimeMillis();
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public Long getCreatedAt() { return createdAt; }
    public void setCreatedAt(Long createdAt) { this.createdAt = createdAt; }
}
