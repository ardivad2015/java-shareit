package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;


    @Test
    public void saveUser() {
        final int collectionSize = userService.getAllUsers().size();
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getName(), newUser.getName());
        assertNotNull(newUser.getId());
        assertEquals(userService.getAllUsers().size(), collectionSize + 1);
    }

    @Test
    public void saveUserThrowsConditionsNotMetException() {
        final int collectionSize = userService.getAllUsers().size();
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name1");
        final UserDto newUser = userService.saveUser(user);
        final UserDto failUser = ObjectsFactory.newUserDto(user.getEmail(), "name1");
        assertThrows(ConditionsNotMetException.class,
                () -> userService.saveUser(failUser));
        assertEquals(userService.getAllUsers().size(), collectionSize + 1);
    }

    @Test
    public void updateUser() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name2");
        final UserDto userNewData = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name11");
        final UserDto newUser = userService.saveUser(user);
        userNewData.setId(newUser.getId());
        final UserDto updatedUser = userService.updateUser(userNewData);
        assertEquals(updatedUser.getEmail(), userNewData.getEmail());
        assertEquals(updatedUser.getName(), userNewData.getName());
    }

    @Test
    public void updateUserThrowsConditionsNotMetException() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name2");
        final UserDto userNewData = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name11");
        final UserDto newUser = userService.saveUser(user);
        userNewData.setId(newUser.getId());
        final UserDto user2 = ObjectsFactory.newUserDto(userNewData.getEmail(), "name2");
        userService.saveUser(user2);
        assertThrows(ConditionsNotMetException.class,
                () -> userService.updateUser(userNewData));
    }

    @Test
    public void updateUserThrowsNotFoundException() {
        final UserDto userNewData = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name11");
        userNewData.setId(99999L);
        assertThrows(NotFoundException.class,
                () -> userService.updateUser(userNewData));
    }

    @Test
    public void getUser() {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name3");
        final UserDto newUser = userService.saveUser(user);
        final UserDto userById = userService.getUser(newUser.getId());
        assertEquals(userById.getEmail(), user.getEmail());
        assertEquals(userById.getName(), user.getName());
    }

    @Test
    public void getUserException() {
        assertThrows(NotFoundException.class,
                () -> userService.getUser(9999L));
    }

}