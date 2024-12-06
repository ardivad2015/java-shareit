package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemEnhancedDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemService {

    ItemDto getById(Long id);

    ItemEnhancedDto getByIdWithComments(Long id);

    List<ItemEnhancedDto> getByOwner(Long userId);

    List<ItemDto> searchItems(String text);

    NewItemDto addNewItem(NewItemDto item, Long ownerId);

    ItemDto updateItem(ItemDto item, Long userId);

    void existsById(Long id);

    void checkPermission(Item item, Long userId);

    CommentDto addNewComment(CommentDto comment, Long itemId, Long userId);

    List<CommentDto> getItemsComments(Set<Long> itemSet);
}
