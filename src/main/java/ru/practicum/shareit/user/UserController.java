package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final UserMapper userMapper;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") @Positive Long userId) {
        return userMapper.toUserDto(userService.getById(userId));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = userMapper.toUser(userDto);
        return userMapper.toUserDto(userService.save(user));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") @Positive Long userId, @RequestBody UserDto userDto) {
        if (Objects.isNull(userDto.getEmail()) && Objects.isNull(userDto.getName())) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    "Должно быть заполнено email или name");
        }
        User user = userMapper.toUser(userDto);
        user.setId(userId);
        return userMapper.toUserDto(userService.update(user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Positive Long userId) {
        userService.delete(userId);
    }
}
