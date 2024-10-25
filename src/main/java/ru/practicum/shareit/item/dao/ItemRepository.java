package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

public interface ItemRepository {

    Item save(Item item);
}
