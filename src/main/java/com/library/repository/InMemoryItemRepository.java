package com.library.repository;

import com.library.model.LibraryItem;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryItemRepository implements ItemRepository {

    private final Map<String, LibraryItem> items = new HashMap<>();

    @Override
    public void save(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Item must not be null");
        }
        items.put(item.getId(), item);
    }

    @Override
    public Optional<LibraryItem> findById(String id) {
        return Optional.ofNullable(items.get(id));
    }

    @Override
    public boolean existsById(String id) {
        return items.containsKey(id);
    }
}
