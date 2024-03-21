package edu.java.scrapper.dao;

import edu.java.scrapper.dto.Chat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Repository
public class ChatLocalDao implements Dao<Chat, Long> {
    private final Map<Long, Chat> storage;

    public ChatLocalDao() {
        this.storage = new HashMap<>();
    }

    @Override
    public Chat get(Long id) {
        return storage.get(id);
    }

    @Override
    public List<Chat> getAll() {
        return List.copyOf(storage.values());
    }

    @Override
    public void save(Chat entity) {
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
