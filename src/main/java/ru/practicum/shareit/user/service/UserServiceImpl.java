package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkBeforeSaving(userDto);
        User user = UserMapper.DtoToUser(userDto);
        repository.save(user);
        return UserMapper.userToDto(user);
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
}