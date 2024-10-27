package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ItemService {

    ItemDto saveItem(ItemDto itemDto);
    ItemDto updateItem(ItemDto itemDto);
    ItemDto getItem(Long id);
    List<ItemDto> getUsersItems(Long userId);
    List<ItemDto> searchItems(String text);
}






