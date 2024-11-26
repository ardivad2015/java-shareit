package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

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
        return userService.getById(userId);
    }

    @PostMapping
    public UserDto addNewUser(@Valid @RequestBody UserDto userDto) {
        return userService.addNew(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable("id") @Positive Long userId, @RequestBody UserDto userDto) {
        if (Objects.isNull(userDto.getEmail()) && Objects.isNull(userDto.getName())) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    "Должно быть заполнено email или name");
        }
        userDto.setId(userId);
        return userService.update(userDto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Positive Long userId) {
        userService.delete(userId);
    }
}
