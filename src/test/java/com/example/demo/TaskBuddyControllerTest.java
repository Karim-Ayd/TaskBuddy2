package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskBuddyController.class)
class TaskBuddyControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @MockitoBean
    private TaskbuddyRepository repository;

    // Helper: baut eine Taskbuddy mit ID (ohne setId, per Reflection)
    private Taskbuddy task(long id, String title, boolean done, long createdAt) {
        Taskbuddy t = new Taskbuddy();
        ReflectionTestUtils.setField(t, "id", id);
        t.setTitle(title);
        t.setDone(done);
        t.setCreatedAt(createdAt);
        return t;
    }

    // 1) GET /api/todos -> liefert Liste
    @Test
    void getTodos_returnsList() throws Exception {
        when(repository.findAll()).thenReturn(List.of(
                task(1, "A", false, 1111L),
                task(2, "B", true,  2222L)
        ));

        mvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].title").value("A"))
                .andExpect(jsonPath("$[0].done").value(false))
                .andExpect(jsonPath("$[0].createdAt").value(1111L));
    }

    // 2) GET /api/todos -> leer wenn nix da
    @Test
    void getTodos_returnsEmptyList_whenNone() throws Exception {
        when(repository.findAll()).thenReturn(List.of());

        mvc.perform(get("/api/todos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    // 3) POST /api/todos -> erstellt Task, 201
    @Test
    void postTodo_createsTodo_returns201() throws Exception {
        // Mock: save gibt Objekt zurück inkl. ID
        when(repository.save(any(Taskbuddy.class))).thenAnswer(inv -> {
            Taskbuddy in = inv.getArgument(0);
            ReflectionTestUtils.setField(in, "id", 10L);
            return in;
        });

        Map<String, Object> body = Map.of(
                "title", "Test",
                "done", false,
                "createdAt", 1234
        );

        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("Test"))
                .andExpect(jsonPath("$.done").value(false))
                .andExpect(jsonPath("$.createdAt").value(1234));

        verify(repository, times(1)).save(any(Taskbuddy.class));
    }

    // 4) POST /api/todos -> title wird getrimmt
    @Test
    void postTodo_trimsTitle() throws Exception {
        when(repository.save(any(Taskbuddy.class))).thenAnswer(inv -> {
            Taskbuddy in = inv.getArgument(0);
            ReflectionTestUtils.setField(in, "id", 11L);
            return in;
        });

        Map<String, Object> body = Map.of(
                "title", "   Hello   ",
                "done", false,
                "createdAt", 5000
        );

        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Hello"));
    }

    // 5) POST /api/todos -> createdAt null => Controller setzt es (nicht null)
    @Test
    void postTodo_setsCreatedAt_whenMissing() throws Exception {
        when(repository.save(any(Taskbuddy.class))).thenAnswer(inv -> {
            Taskbuddy in = inv.getArgument(0);
            ReflectionTestUtils.setField(in, "id", 12L);
            return in;
        });

        Map<String, Object> body = Map.of(
                "title", "X",
                "done", false
                // createdAt fehlt
        );

        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(12))
                .andExpect(jsonPath("$.title").value("X"))
                .andExpect(jsonPath("$.createdAt").isNumber()); // Hauptpunkt: existiert & ist Zahl
    }

    // 6) POST /api/todos -> leere/blank title => 400
    @Test
    void postTodo_rejectsBlankTitle_400() throws Exception {
        Map<String, Object> body = Map.of(
                "title", "   ",
                "done", false
        );

        mvc.perform(post("/api/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isBadRequest());

        verify(repository, never()).save(any());
    }

    // 7) PUT /api/todos/{id} -> 404 wenn nicht gefunden
    @Test
    void putTodo_returns404_whenNotFound() throws Exception {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        Map<String, Object> body = Map.of(
                "title", "New",
                "done", true
        );

        mvc.perform(put("/api/todos/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isNotFound());

        verify(repository, never()).save(any());
    }

    // 8) PUT /api/todos/{id} -> updated title + done, createdAt bleibt
    @Test
    void putTodo_updatesTitleAndDone_preservesCreatedAt() throws Exception {
        Taskbuddy existing = task(5, "Old", false, 7777L);
        when(repository.findById(5L)).thenReturn(Optional.of(existing));

        when(repository.save(any(Taskbuddy.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> body = Map.of(
                "title", "  New Title  ",
                "done", true
        );

        mvc.perform(put("/api/todos/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.title").value("New Title"))
                .andExpect(jsonPath("$.done").value(true))
                .andExpect(jsonPath("$.createdAt").value(7777L));
    }

    // 9) PUT /api/todos/{id} -> blank title überschreibt nicht (bleibt alt), done wird gesetzt
    @Test
    void putTodo_blankTitle_doesNotOverwriteTitle() throws Exception {
        Taskbuddy existing = task(6, "KeepMe", false, 8888L);
        when(repository.findById(6L)).thenReturn(Optional.of(existing));

        when(repository.save(any(Taskbuddy.class))).thenAnswer(inv -> inv.getArgument(0));

        Map<String, Object> body = Map.of(
                "title", "   ",
                "done", true
        );

        mvc.perform(put("/api/todos/6")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("KeepMe"))
                .andExpect(jsonPath("$.done").value(true));
    }

    // 10) DELETE /api/todos/{id} -> 204 wenn existiert
    @Test
    void deleteTodo_returns204_whenExists() throws Exception {
        when(repository.existsById(7L)).thenReturn(true);
        doNothing().when(repository).deleteById(7L);

        mvc.perform(delete("/api/todos/7"))
                .andExpect(status().isNoContent());

        verify(repository, times(1)).deleteById(7L);
    }
}
