package edu.java.bot.dao;

import edu.java.bot.dto.User;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class UserLocalDao implements Dao<User, Long> {
    private final Map<Long, User> storage;

    public UserLocalDao() {
        this.storage = new HashMap<>();
    }

    @Override
    public User get(Long id) {
        return storage.get(id);
    }

    @Override
    public List<User> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void save(User entity) {
        storage.put(entity.getId(), entity);
    }

    @Override
    public void delete(Long id) {
        storage.remove(id);
    }

    @Override
    public boolean contains(Long id) {
        return storage.containsKey(id);
    }
}
