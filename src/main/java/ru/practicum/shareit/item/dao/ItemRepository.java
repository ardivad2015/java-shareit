package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Item update(Item item);

    Optional<Item> findById(Long id);

    List<Item> findByOwner(Long userId);

    List<Item> findByText(String text);
}
