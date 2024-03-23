package edu.java.scrapper.dao;

import java.util.List;

public interface Dao<E, K> {
    E get(K id);

    List<E> getAll();

    void save(E entity);

    void delete(K id);

    boolean contains(K id);
}
