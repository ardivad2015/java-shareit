package ru.practicum.shareit.item.dto.mapper;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class ItemMapper {

    public static Item dtoToItem(ItemDto itemDto) {
       Item item = new Item();
       item.setId(itemDto.getId());
       item.setName(itemDto.getName());
       item.setDescription(itemDto.getDescription());
       item.setAvailable(itemDto.getAvailable());
       item.setOwner(itemDto.getOwner());
       return item;
    }

    public static ItemDto itemToDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setOwner(item.getOwner());
        return itemDto;
    }
}
