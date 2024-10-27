package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
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
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::userToDto)
                .toList();
    }

    @Override
    public UserDto getUser(Long id) {
        return UserMapper.userToDto(userRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Пользователь с id = %d не найден", id))));
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkBeforeSaving(userDto);
        final User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        checkBeforeUpdating(userDto);
        final User user = UserMapper.dtoToUser(userDto);
        return UserMapper.userToDto(userRepository.update(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (userRepository.delete(id).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    private void checkBeforeSaving(UserDto userDto) {
        final List<String> errorList = new ArrayList<>();
        final Optional<User> currentUser = userRepository.findByEmail(userDto.getEmail());
        currentUser.ifPresent(user -> errorList.add(String.format("Email %s уже зарегистрирован у пользователя с id %d",
                userDto.getEmail(), user.getId())));
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }

    private void checkBeforeUpdating(UserDto userDto) {
        final List<String> errorList = new ArrayList<>();
        final Long userId = userDto.getId();
        getUser(userId);
        final String Email = userDto.getEmail();
        if (Objects.nonNull(Email) && !Email.isBlank()) {
            final Optional<User> currentUserOptional = userRepository.findByEmail(userDto.getEmail());
            if (currentUserOptional.isPresent()) {
                User user = currentUserOptional.get();
                if (!user.getId().equals(userId)) {
                    errorList.add(String.format("Email %s уже зарегистрирован у пользователя с id %d",
                            userDto.getEmail(), user.getId()));
                }
            }
        }
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }
}