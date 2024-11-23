package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public void ExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    @Transactional
    public User save(User user) {
        checkEmail(user.getEmail());
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public User update(User updatedUser) {
        User user = getById(updatedUser.getId());
        checkEmail(updatedUser.getEmail());
        final String email = updatedUser.getEmail();
        if (Objects.nonNull(email) && !email.isBlank()) {
            user.setEmail(email);
        }
        final String name = updatedUser.getName();
        if (Objects.nonNull(name) && !name.isBlank()) {
            user.setName(name);
        }
        return user;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void checkEmail(String email) {
        if (Objects.isNull(email) || email.isBlank()) {
            return;
        }
        if (userRepository.existsByEmail(email)) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    String.format("Email %s уже зарегистрирован ", email));
        }
    }
}