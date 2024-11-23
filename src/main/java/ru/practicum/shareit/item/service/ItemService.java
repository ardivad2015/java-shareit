package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getById(Long id);

    List<Item> searchItems(String text);

    List<Item> getByOwner(Long userId);

    Item save(Item item);

    Item update(Item item, Long userId);

    void existsById(Long id);

    boolean userIsOwner(Item item, Long userId);
}
