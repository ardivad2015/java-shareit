package ru.practicum.shareit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BaseInMemoryRepository<T> {

    private final Map<Long, T> storage = new HashMap<>();
    private Long id = 0L;

    public List<T> getFromStorage() {
        return storage.values().stream().toList();
    }

    public Long putInStorage(T t) {
        id = id + 1;
        storage.put(id, t);
        return id;
    }

    public void updateInStorage(Long id, T t) {
        storage.put(id, t);
    }

    public Optional<T> getById(Long id) {
        return Optional.ofNullable(storage.get(id));
    }

    public Optional<T> delete(Long id) {
        return Optional.ofNullable(storage.remove(id));
    }
}
