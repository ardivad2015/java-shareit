package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public UserDto getById(Long id) {
        return userMapper.toUserDto(findById(id));
    }

    @Override
    public void ExistsById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException(String.format("Пользователь с id = %d не найден", id));
        }
    }

    @Override
    @Transactional
    public UserDto addNew(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    String.format("Email %s уже зарегистрирован ", userDto.getEmail()));
        }

        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public UserDto update(UserDto updatedUser) {
        final User user = findById(updatedUser.getId());
        final String email = updatedUser.getEmail();

        if (Objects.nonNull(email) && !email.isBlank()) {
            userRepository.findByEmail(updatedUser.getEmail()).map(User::getId)
                    .ifPresent(userId -> {
                        if (!userId.equals(updatedUser.getId())) {
                            throw ConditionsNotMetException.simpleConditionsNotMetException(
                                    String.format("Email %s уже зарегистрирован у пользователя с id %d",
                                            email, userId));
                        }
                    });
            user.setEmail(email);
        }

        final String name = updatedUser.getName();

        if (Objects.nonNull(name) && !name.isBlank()) {
            user.setName(name);
        }
        return userMapper.toUserDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

   private User findById(Long userId) {
       return userRepository.findById(userId).orElseThrow(() ->
               new NotFoundException(String.format("Пользователь с id = %d не найден", userId)));
   }
}