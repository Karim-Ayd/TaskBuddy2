package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class TaskbuddyRepositoryTest {

    @Autowired
    TaskbuddyRepository repository;

    @Test
    void saveAndFindById_works() {
        Taskbuddy t = new Taskbuddy("Repo-Test", false);
        t.setCreatedAt(111L);

        Taskbuddy saved = repository.save(t);

        assertNotNull(saved.getId());

        Optional<Taskbuddy> loaded = repository.findById(saved.getId());
        assertTrue(loaded.isPresent());
        assertEquals("Repo-Test", loaded.get().getTitle());
        assertFalse(loaded.get().isDone());
        assertEquals(111L, loaded.get().getCreatedAt());
    }

    @Test
    void deleteById_removesEntity() {
        Taskbuddy saved = repository.save(new Taskbuddy("Delete-Me", false));
        Long id = saved.getId();

        assertTrue(repository.existsById(id));

        repository.deleteById(id);

        assertFalse(repository.existsById(id));
    }
}
