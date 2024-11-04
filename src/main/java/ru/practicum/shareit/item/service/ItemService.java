package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import java.util.List;

public interface ItemService {

    ItemDto saveItem(ItemDto itemDto);

    ItemDto updateItem(ItemDto itemDto);

    ItemDto getItem(Long id);

    List<ItemDto> getUsersItems(Long userId);

    List<ItemDto> searchItems(String text);
}