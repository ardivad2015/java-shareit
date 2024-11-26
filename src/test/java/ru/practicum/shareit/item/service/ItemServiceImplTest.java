package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingMapperImpl;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;

    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();


    @Test
    public void addNewItem_whenUserFound_callRepositorySave() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, commentMapper, bookingMapper, userMapper);
        final ItemDto itemDto = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(), ObjectsFactory.newStringValue());
        final UserDto owner = ObjectsFactory.newUserDto("email", "name");

        when(userService.getById(1L)).thenReturn(owner);

        final ItemDto actualItem = itemService.addNewItem(itemDto, 1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
    }

    @Test
    public void addNewItem_whenUserNotFound_NotFoundExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, commentMapper, bookingMapper, userMapper);
        final ItemDto itemDto = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(), ObjectsFactory.newStringValue());

        when(userService.getById(1L)).thenReturn(Optional.empty());

        final ItemDto actualItem = itemService.addNewItem(itemDto, 1L);

        Mockito.verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
    }
}