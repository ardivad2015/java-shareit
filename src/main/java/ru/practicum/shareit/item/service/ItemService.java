package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    Item getById(Long id);

    List<Item> searchItems(String text);

    List<Item> getUsersItems(Long userId);

    Item save(Item item, Long userId);

    Item update(Item item, Long userId);
}
