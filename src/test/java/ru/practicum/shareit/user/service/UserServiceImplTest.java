package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    private final UserMapper userMapper = new UserMapperImpl();


    @Test
    public void addNew_whenEmailUnique_callRepositorySave() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");

        when(userRepository.existsByEmail(userDto.getEmail()))
                .thenReturn(false);

        final UserDto actualUser = userService.addNew(userDto);

        verify(userRepository, Mockito.times(1))
                .save(any(User.class));
    }

    @Test
    public void addNew_whenEmailNotUnique_thenConditionsNotMetExceptionThrown() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");

        when(userRepository.existsByEmail(userDto.getEmail()))
                .thenReturn(true);

        assertThrows(ConditionsNotMetException.class, () -> userService.addNew(userDto));
        verify(userRepository, Mockito.never())
                .save(any(User.class));

    }

    @Test
    public void update_whenUserExistAndEmailUnique_thenUpdatedFields() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");
        final Long id = 1L;
        final User user = ObjectsFactory.newUser(ObjectsFactory.newStringValue(), "User1");

        userDto.setId(id);
        user.setId(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userDto.getEmail()))
                .thenReturn(Optional.of(user));

        final UserDto actualUser = userService.update(userDto);

        verify(userRepository).save(userArgumentCaptor.capture());

        final User savedUser = userArgumentCaptor.getValue();

        assertEquals(savedUser.getEmail(), userDto.getEmail());
        assertEquals(savedUser.getName(), userDto.getName());
        assertEquals(savedUser.getId(), userDto.getId());
        verify(userRepository, Mockito.times(1))
                .save(user);
    }

    @Test
    public void update_whenUserNotExist_thenNotFoundExceptionThrown() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");
        final Long id = 1L;

        userDto.setId(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.update(userDto));
        verify(userRepository, Mockito.never())
                .save(any(User.class));
    }

    @Test
    public void update_whenEmailNotUnique_thenConditionsNotMetExceptionThrown() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");
        final Long id = 1L;
        final User user = ObjectsFactory.newUser(ObjectsFactory.newStringValue(), "User1");
        final User user2 = ObjectsFactory.newUser(ObjectsFactory.newStringValue(), "User1");

        userDto.setId(id);
        user.setId(id);
        user2.setId(id + 1);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));
        when(userRepository.findByEmail(userDto.getEmail()))
                .thenReturn(Optional.of(user2));

        assertThrows(ConditionsNotMetException.class, () -> userService.update(userDto));
        verify(userRepository, Mockito.never())
                .save(any(User.class));
    }

    @Test
    public void existById_whenUserFound_DoesNotThrowNotFoundException() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final Long id = 1L;

        when(userRepository.existsById(id))
                .thenReturn(true);

        assertDoesNotThrow(() -> userService.existsById(id));
    }

    @Test
    public void existById_whenUserNotFound_NotFoundExceptionThrown() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final Long id = 1L;

        when(userRepository.existsById(id))
                .thenReturn(false);

        assertThrows(NotFoundException.class, () -> userService.existsById(id));
    }

    @Test
    public void getById_whenUserFound_returnedUserDto() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");
        final User user = userMapper.toUser(userDto);
        final Long id = 1L;

        userDto.setId(id);
        user.setId(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.of(user));

        UserDto actualUser = userService.getById(id);

        assertEquals(actualUser.getEmail(), userDto.getEmail());
        assertEquals(actualUser.getName(), userDto.getName());
        assertEquals(actualUser.getId(), userDto.getId());
    }

    @Test
    public void getById_whenUserNotFound_NotFoundExceptionThrown() {
        final UserService userService = new UserServiceImpl(userRepository, userMapper);
        final UserDto userDto = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "UserDto1");
        final User user = userMapper.toUser(userDto);
        final Long id = 1L;

        userDto.setId(id);
        user.setId(id);

        when(userRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(id));
    }

}