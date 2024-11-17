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
@Transactional(readOnly = true)
@RequiredArgsConstructor
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", id)));
    }

    @Transactional
    @Override
    public User save(User user) {
        checkBeforeSave(user);
        return userRepository.save(user);
    }

    @Transactional
    @Override
    public User update(User updatedUser) {
        User user = getById(updatedUser.getId());
        checkBeforeUpdate(updatedUser);
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

    @Transactional
    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    private void checkBeforeSave(User newUser) {
        final List<String> errorList = new ArrayList<>();
        userRepository.findByEmail(newUser.getEmail())
        .ifPresent(user -> errorList.add(String.format("Email %s уже зарегистрирован у пользователя с id %d",
                newUser.getEmail(), user.getId())));
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }

    private void checkBeforeUpdate(User updatedUser) {
        final List<String> errorList = new ArrayList<>();
        userRepository.findByEmail(updatedUser.getEmail())
        .ifPresent(user -> {
            if (!user.getId().equals(updatedUser.getId())) {
                errorList.add(String.format("Email %s уже зарегистрирован у пользователя с id %d",
                        updatedUser.getEmail(), user.getId()));
            }
        });
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }
}