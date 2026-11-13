package com.library.repository;

import com.library.model.Patron;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPatronRepository implements PatronRepository {

    private final Map<String, Patron> patrons = new HashMap<>();

    @Override
    public void save(Patron patron) {
        patrons.put(patron.getId(), patron);
    }

    @Override
    public Optional<Patron> findById(String id) {
        return Optional.ofNullable(patrons.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return patrons.containsKey(id);
    }
}
