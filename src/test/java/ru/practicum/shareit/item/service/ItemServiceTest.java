package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ItemServiceTest {

    @Autowired
    ItemService itemService;
    @Autowired
    UserService userService;

    @Test
    public void saveOKWhenUserExists() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        final ItemDto newItem = itemService.saveItem(item);
        assertEquals(item.getName(), newItem.getName());
        assertEquals(item.getDescription(), newItem.getDescription());
        assertNotNull(newItem.getId());
    }

    @Test
    public void saveThrowNotFoundWhenUserNotExists() {
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 9999L);
        assertThrows(NotFoundException.class,
                () -> itemService.saveItem(item));
    }

    @Test
    public void updateOKWhenOwnersAreEquals() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        final ItemDto newItem = itemService.saveItem(item);
        final ItemDto itemNewData = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        itemNewData.setId(newItem.getId());
        final ItemDto updatedItem = itemService.updateItem(itemNewData);
        assertEquals(itemNewData.getName(), updatedItem.getName());
        assertEquals(itemNewData.getDescription(), updatedItem.getDescription());
    }

    @Test
    public void updateThrowConditionsNotMetExceptionWhenOwnersAreNotEquals() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());

        final UserDto user2 = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser2 = userService.saveUser(user2);
        final ItemDto newItem = itemService.saveItem(item);
        final ItemDto itemNewData = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser2.getId());
        itemNewData.setId(newItem.getId());
        assertThrows(ConditionsNotMetException.class,
                () -> itemService.updateItem(itemNewData));
    }

    @Test
    public void getItemById() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        final ItemDto newItem = itemService.saveItem(item);
        final ItemDto foundedItem = itemService.getItem(newItem.getId());
        assertEquals(newItem.getName(), foundedItem.getName());
    }

    @Test
    public void getUsersItems() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final UserDto user2 = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser2 = userService.saveUser(user2);

        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        final ItemDto newItem = itemService.saveItem(item);
        final ItemDto item2 = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser2.getId());
        final ItemDto newItem2 = itemService.saveItem(item2);
        final List<ItemDto> items = itemService.getUsersItems(newUser2.getId());
        assertEquals(items.size(), 1);
        assertEquals(items.getFirst().getId(), newItem2.getId());
    }

    @Test
    public void findByText() {
        final String text = "text_search";
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);

        final ItemDto item = ObjectsFactory.newItemDto(
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        item.setAvailable(false);
        item.setId(itemService.saveItem(item).getId());
        final ItemDto item2 = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(), newUser.getId());
        item2.setAvailable(true);
        item2.setId(itemService.saveItem(item2).getId());
        final ItemDto item3 = ObjectsFactory.newItemDto(
                ObjectsFactory.newStringValue() + text + ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), newUser.getId());
        item3.setAvailable(true);
        item3.setId(itemService.saveItem(item3).getId());
        final ItemDto item4 = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(), ObjectsFactory.newStringValue(),
                newUser.getId());
        item4.setAvailable(true);
        item4.setId(itemService.saveItem(item4).getId());

        List<ItemDto> items = itemService.searchItems(text);
        assertEquals(items.size(), 2);
        assertTrue(items.stream().map(ItemDto::getId).toList().containsAll(List.of(item2.getId(), item3.getId())));
    }


}