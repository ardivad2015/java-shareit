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
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InsufficientPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

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
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Captor
    ArgumentCaptor<Item> itemArgumentCaptor;
    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;
    @Captor
    ArgumentCaptor<Comment> commentArgumentCaptor;

    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final CommentMapper commentMapper = new CommentMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    public void addNewItem_whenUserFound_callRepositorySaveWithNullRequestId() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final NewItemDto itemDto = ObjectsFactory.newNewItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue());
        final UserDto owner = ObjectsFactory.newUserDto("email", "name");

        when(userService.getById(1L)).thenReturn(owner);

        final NewItemDto actualItem = itemService.addNewItem(itemDto, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        final Item savedItem = itemArgumentCaptor.getValue();

        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
        assertEquals(savedItem.getName(), itemDto.getName());
        assertEquals(savedItem.getOwner().getEmail(), owner.getEmail());
        assertNull(savedItem.getItemRequest());
    }

    @Test
    public void addNewItem_whenUserFoundAndRequestNonull_callRepositorySaveWithRequestId() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final NewItemDto itemDto = ObjectsFactory.newNewItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue());
        final UserDto owner = ObjectsFactory.newUserDto("email", "name");
        final User author = ObjectsFactory.newUser("email2", "name2");
        final ItemRequest itemRequest = new ItemRequest();

        author.setId(2L);
        itemRequest.setId(1L);
        itemRequest.setAuthor(author);
        itemDto.setRequestId(1L);

        when(userService.getById(1L)).thenReturn(owner);
        when(itemRequestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));

        final NewItemDto actualItem = itemService.addNewItem(itemDto, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        final Item savedItem = itemArgumentCaptor.getValue();

        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
        assertEquals(savedItem.getName(), itemDto.getName());
        assertEquals(savedItem.getOwner().getEmail(), owner.getEmail());
        assertEquals(savedItem.getItemRequest().getId(), itemRequest.getId());
    }

    @Test
    public void addNewItem_whenUserNotFound_NotFoundExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final NewItemDto itemDto = ObjectsFactory.newNewItemDto(ObjectsFactory.newStringValue(), ObjectsFactory.newStringValue());

        when(userService.getById(1L)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> itemService.addNewItem(itemDto, 1L));
        verify(itemRepository, Mockito.never())
                .save(any(Item.class));
    }

    @Test
    public void updateItem_whenUserFoundAndItsOwner_updateFields() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final ItemDto itemDto = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue());
        final Item item = itemMapper.toItem(itemDto);
        final User owner = ObjectsFactory.newUser("email", "name");

        itemDto.setId(1L);
        owner.setId(1L);
        item.setId(1L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ItemDto actualItem = itemService.updateItem(itemDto, 1L);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        final Item savedItem = itemArgumentCaptor.getValue();

        verify(itemRepository, Mockito.times(1))
                .save(any(Item.class));
        assertEquals(savedItem.getName(), itemDto.getName());
        assertEquals(savedItem.getOwner().getEmail(), owner.getEmail());
    }

    @Test
    public void updateItem_whenUserIsNotOwner_thenInsufficientPermissionExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final ItemDto itemDto = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue());
        final Item item = itemMapper.toItem(itemDto);
        final User user = ObjectsFactory.newUser("email1", "name1");
        final User owner = ObjectsFactory.newUser("email2", "name2");

        itemDto.setId(1L);
        owner.setId(2L);
        user.setId(1L);
        item.setId(1L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(InsufficientPermissionException.class, () -> itemService.updateItem(itemDto, 1L));
        verify(itemRepository, Mockito.never())
                .save(any(Item.class));
    }

    @Test
    public void getByOwner_whenUserFound_returnItemWithComments() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");
        final User author = ObjectsFactory.newUser("emailauthor", "nameauthor");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");
        final LocalDateTime currentTime = LocalDateTime.now();

        owner.setId(1L);
        author.setId(2L);
        booker.setId(3L);
        item.setId(1L);
        item.setOwner(owner);

        final Comment comment = new Comment();

        comment.setId(1L);
        comment.setItem(item);
        comment.setText("comment");
        comment.setCreated(currentTime);
        comment.setAuthor(author);

        final Booking lastBooking = new Booking();
        final Booking nextBooking = new Booking();

        lastBooking.setId(1L);
        lastBooking.setBooker(booker);
        lastBooking.setItem(item);
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setStart(currentTime.minusDays(1).minusMinutes(60));
        lastBooking.setEnd(currentTime.minusDays(1));

        nextBooking.setId(2L);
        nextBooking.setBooker(booker);
        nextBooking.setItem(item);
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setStart(currentTime.plusDays(1));
        nextBooking.setEnd(currentTime.plusDays(1).plusMinutes(60));

        when(itemRepository.findAllByOwnerId(1L)).thenReturn(List.of(item));
        when(commentRepository.findAllByItemIdInWithAuthorEagerly(Set.of(1L))).thenReturn(List.of(comment));
        when(bookingRepository.findAllByItemIdInAndStatus(Set.of(1L), BookingStatus.APPROVED))
                .thenReturn(List.of(lastBooking, nextBooking));

        final List<ItemEnhancedDto> items = itemService.getByOwner(1L);
        assertEquals(items.size(), 1);

        final ItemEnhancedDto actualItem = items.getFirst();
        assertEquals(actualItem.getId(), 1L);
        assertNotNull(actualItem.getLastBooking());
        assertNotNull(actualItem.getNextBooking());
        assertNotNull(actualItem.getComments());
        assertEquals(actualItem.getLastBooking().getStart(), lastBooking.getStart());
        assertEquals(actualItem.getLastBooking().getEnd(), lastBooking.getEnd());
        assertEquals(actualItem.getNextBooking().getStart(), nextBooking.getStart());
        assertEquals(actualItem.getNextBooking().getEnd(), nextBooking.getEnd());
        assertEquals(actualItem.getComments().size(), 1);
        assertEquals(actualItem.getComments().getFirst().getId(), 1L);
        assertEquals(actualItem.getComments().getFirst().getText(), comment.getText());
    }

    @Test
    public void existsById_whenItemNotFound_thenNotFoundExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);

        when(itemRepository.existsById(1L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.existsById(1L));
    }

    @Test
    public void getByIdWithComments_whenItemFound_thenReturnItemWithComments() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");
        final User author = ObjectsFactory.newUser("emailauthor", "nameauthor");
        final LocalDateTime currentTime = LocalDateTime.now();

        owner.setId(1L);
        author.setId(2L);
        item.setId(1L);
        item.setOwner(owner);

        final Comment comment = new Comment();

        comment.setId(1L);
        comment.setItem(item);
        comment.setText("comment");
        comment.setCreated(currentTime);
        comment.setAuthor(author);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemIdInWithAuthorEagerly(Set.of(1L))).thenReturn(List.of(comment));

        final ItemEnhancedDto actualItem = itemService.getByIdWithComments(1L);

        assertEquals(actualItem.getId(), 1L);
        assertNotNull(actualItem.getComments());
        assertEquals(actualItem.getComments().size(), 1);
        assertEquals(actualItem.getComments().getFirst().getId(), 1L);
        assertEquals(actualItem.getComments().getFirst().getText(), comment.getText());
    }

    @Test
    public void getById_whenItemFound_thenReturnItem() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");

        owner.setId(1L);
        item.setId(1L);
        item.setOwner(owner);

        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        final ItemDto actualItem = itemService.getById(1L);

        assertEquals(actualItem.getId(), 1L);
        assertEquals(actualItem.getName(), item.getName());
    }

    @Test
    public void getById_whenItemNotFound_thenNotFoundExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        when(itemRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getById(1L));
    }

    @Test
    public void searchItems_whenTextNotBlank_thenCallRepository() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);

        final List<ItemDto> items = itemService.searchItems("text");

        verify(itemRepository).findAllByNameLikeOrDescriptionLikeAndAvailable(stringArgumentCaptor.capture());
        final String searchText = stringArgumentCaptor.getValue();

        assertEquals(searchText, "text");
    }

    @Test
    public void searchItems_whenTextBlank_thenReturnEmptyList() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);

        final List<ItemDto> items = itemService.searchItems("");

        verify(itemRepository, Mockito.never())
                .findAllByNameLikeOrDescriptionLikeAndAvailable(anyString());
        assertEquals(items.size(), 0);
    }

    @Test
    public void checkPermission_whenUserIdEqualsOwnerId_thenNoExceptionsThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");

        owner.setId(1L);
        item.setId(1L);
        item.setOwner(owner);

        itemService.checkPermission(item, 1L);
    }

    @Test
    public void checkPermission_whenUserIdNotEqualsOwnerId_thenInsufficientPermissionExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");

        owner.setId(1L);
        item.setId(1L);
        item.setOwner(owner);

        assertThrows(InsufficientPermissionException.class, () -> itemService.checkPermission(item, 2L));
    }

    @Test
    public void addNewComment_whenUserFoundAndBookingWas_thenCallRepositorySave() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");
        final User author = ObjectsFactory.newUser("emailauthor", "nameauthor");

        owner.setId(1L);
        author.setId(2L);
        item.setId(1L);
        item.setOwner(owner);

        final CommentDto commentDto = new CommentDto();

        commentDto.setText("comment");

        when(userService.getById(2L)).thenReturn(userMapper.toUserDto(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(1L), eq(2L),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(true);


        final CommentDto actualComment = itemService.addNewComment(commentDto, 1L, 2L);

        verify(commentRepository).save(commentArgumentCaptor.capture());
        final Comment savedComment = commentArgumentCaptor.getValue();

        assertEquals(savedComment.getItem().getId(), item.getId());
        assertEquals(savedComment.getItem().getName(), item.getName());
        assertEquals(savedComment.getAuthor().getId(), author.getId());
        assertEquals(savedComment.getText(), commentDto.getText());
    }

    @Test
    public void addNewComment_whenUserFoundAndBookingWasNot_thenBadRequestExceptionThrown() {
        final ItemService itemService = new ItemServiceImpl(itemRepository, userService, bookingRepository, itemMapper,
                commentRepository, itemRequestRepository, commentMapper, bookingMapper, userMapper);
        final Item item = ObjectsFactory.newItem("item", "item");
        final User owner = ObjectsFactory.newUser("emailowner", "owner");
        final User author = ObjectsFactory.newUser("emailauthor", "nameauthor");

        owner.setId(1L);
        author.setId(2L);
        item.setId(1L);
        item.setOwner(owner);

        final CommentDto commentDto = new CommentDto();

        commentDto.setText("comment");

        when(userService.getById(2L)).thenReturn(userMapper.toUserDto(author));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));
        when(bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(eq(1L), eq(2L),
                eq(BookingStatus.APPROVED), any(LocalDateTime.class))).thenReturn(false);

        assertThrows(BadRequestException.class, () -> itemService.addNewComment(commentDto, 1L, 2L));
        verify(commentRepository, never()).save(any(Comment.class));
    }
}