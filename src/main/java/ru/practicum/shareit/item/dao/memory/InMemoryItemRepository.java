package ru.practicum.shareit.item.dao.memory;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.BaseInMemoryRepository;

@Repository
public class InMemoryItemRepository extends BaseInMemoryRepository implements ItemRepository {

    @Override
    public Item save(Item item) {
        Long id = putInStorage(item);
        item.setId(id);
        return item;
    }
}
