package org.example.repository;

import org.example.model.Entity;

import java.util.Collection;
import java.util.Optional;

public interface IRepository<ID,E extends Entity<ID>> {
    Optional<E> find(ID id);
    Iterable<E> findAll();
    Optional<E> save(E entity);
    Optional<E> delete(ID id);
    Optional<E> update(E entity);
}
