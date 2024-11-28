package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto addNew(UserDto user);

    UserDto update(UserDto user);

    UserDto getById(Long id);

    void delete(Long id);

    void existsById(Long id);
}
