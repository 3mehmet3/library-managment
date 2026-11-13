package com.library.repository;

import com.library.model.LibraryItem;
import java.util.Optional;

public interface ItemRepository {

    void save(LibraryItem item);

    Optional<LibraryItem> findById(String id);

    boolean existsById(String id);
}
