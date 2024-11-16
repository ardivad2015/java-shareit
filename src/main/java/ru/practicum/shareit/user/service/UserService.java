package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    User save(User user);

    User update(User user);

    User getById(Long Id);

    void delete(Long Id);
}
