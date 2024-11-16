package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.user.dto.UserDto;
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
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public UserDto getById(@PathVariable("id") @Positive Long userId) {
        return mapToUserDto(userService.getById(userId));
    }

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        User user = mapToUser(userDto);
        return mapToUserDto(userService.save(user));
    }

    @PatchMapping("/{id}")
    public UserDto update(@PathVariable("id") @Positive Long userId, @RequestBody UserDto userDto) {
        if (Objects.isNull(userDto.getEmail()) && Objects.isNull(userDto.getName())) {
            throw ConditionsNotMetException.simpleConditionsNotMetException(
                    "Должно быть заполнено email или name");
        }
        User user = mapToUser(userDto);
        user.setId(userId);
        return mapToUserDto(userService.update(user));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") @Positive Long userId) {
        userService.delete(userId);
    }

    public User mapToUser(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }
    public UserDto mapToUserDto(User user) {
        return modelMapper.map(user, UserDto.class);
    }


}
