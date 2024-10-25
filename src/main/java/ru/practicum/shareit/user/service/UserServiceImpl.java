package ru.practicum.shareit.user.service;

import jakarta.validation.constraints.Email;
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

    private final UserRepository repository;

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::userToDto)
                .toList();
    }

    @Override
    public UserDto getUser(Long id) {
        Optional<User> currentUserOptional = repository.findById(id);
        if (currentUserOptional.isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
        return UserMapper.userToDto(currentUserOptional.get());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkBeforeSaving(userDto);
        User user = UserMapper.DtoToUser(userDto);
        return UserMapper.userToDto(repository.save(user));
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        checkBeforeUpdating(userDto);
        User user = UserMapper.DtoToUser(userDto);
        return UserMapper.userToDto(repository.update(user));
    }

    @Override
    public void deleteUser(Long id) {
        if (repository.delete(id).isEmpty()) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    private void checkBeforeSaving(UserDto userDto) {
        final List<String> errorList = new ArrayList<>();
        Optional<User> currentUser = repository.findByEmail(userDto.getEmail());
        currentUser.ifPresent(user -> errorList.add(String.format("Email %s уже зарегистрирован у пользователя с id %d",
                userDto.getEmail(), user.getId())));
        if (!errorList.isEmpty()) {
            throw new ConditionsNotMetException(new ErrorResponse(errorList));
        }
    }

    private void checkBeforeUpdating(UserDto userDto) {
        final List<String> errorList = new ArrayList<>();
        Long userId = userDto.getId();
        getUser(userId);
        String Email = userDto.getEmail();
        if (Objects.nonNull(Email) && !Email.isBlank()) {
            Optional<User> currentUserOptional = repository.findByEmail(userDto.getEmail());
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