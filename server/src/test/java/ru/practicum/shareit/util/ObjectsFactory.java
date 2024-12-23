package ru.practicum.shareit.util;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public class ObjectsFactory {

    public static User newUser(String email,String name) {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static UserDto newUserDto(String email, String name) {
        UserDto user = new UserDto();
        user.setEmail(email);
        user.setName(name);
        return user;
    }

    public static Item newItem(String name, String description) {
        Item item = new Item();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return item;
    }

    public static ItemDto newItemDto(String name, String description) {
        ItemDto item = new ItemDto();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return item;
    }

    public static NewItemDto newNewItemDto(String name, String description) {
        NewItemDto item = new NewItemDto();
        item.setName(name);
        item.setDescription(description);
        item.setAvailable(true);
        return item;
    }

    public static String newStringValue() {
        return java.util.UUID.randomUUID().toString();
    }
}
