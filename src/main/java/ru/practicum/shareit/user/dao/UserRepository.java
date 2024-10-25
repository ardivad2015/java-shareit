package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();
    User save(User user);
    User update(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
    Optional<User> delete(Long id);
}