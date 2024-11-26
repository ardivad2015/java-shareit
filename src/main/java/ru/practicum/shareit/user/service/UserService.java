package ru.practicum.shareit.user.service;

import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto addNew(UserDto user);

    UserDto update(UserDto user);

    UserDto getById(Long Id);

    void delete(Long id);

    void ExistsById(Long id);
}
