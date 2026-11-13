package com.library.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.library.model.Patron;
import com.library.policy.StudentPolicy;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class InMemoryPatronRepositoryTest {

    private final PatronRepository repository = new InMemoryPatronRepository();

    @Test
    void findById_savedPatron_returnsPatron() {
        Patron patron = new Patron("p1", "Ali", new StudentPolicy());
        repository.save(patron);

        assertEquals(Optional.of(patron), repository.findById("p1"));
    }

    @Test
    void findById_unknownId_returnsEmpty() {
        assertEquals(Optional.empty(), repository.findById("missing"));
    }

    @Test
    void existsById_savedPatron_returnsTrue() {
        repository.save(new Patron("p1", "Ali", new StudentPolicy()));

        assertTrue(repository.existsById("p1"));
    }

    @Test
    void existsById_unknownId_returnsFalse() {
        assertFalse(repository.existsById("missing"));
    }
}
