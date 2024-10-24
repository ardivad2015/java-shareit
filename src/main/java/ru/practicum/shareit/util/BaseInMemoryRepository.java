package ru.practicum.shareit.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
