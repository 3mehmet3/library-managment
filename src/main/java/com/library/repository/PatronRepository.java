package com.library.repository;

import com.library.model.Patron;
import java.util.Optional;

public interface PatronRepository {

    void save(Patron patron);

    Optional<Patron> findById(String id);

    boolean existsById(String id);
}
