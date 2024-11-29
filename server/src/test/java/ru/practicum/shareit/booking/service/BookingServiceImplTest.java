package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemService itemService;
    @Captor
    ArgumentCaptor<Booking> bookingArgumentCaptor;

    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    public void addNew_whenUserFoundAndItemAvailable_thenCallRepositorySave() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final BookingRequestDto bookingRequestDto = new BookingRequestDto();
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");

        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);
        bookingRequestDto.setStart(currentTime.plusDays(1));
        bookingRequestDto.setEnd(currentTime.plusDays(2));
        bookingRequestDto.setItemId(item.getId());

        when(userService.getById(2L)).thenReturn(userMapper.toUserDto(booker));
        when(itemService.getById(1L)).thenReturn(itemMapper.toItemDto(item));

        final BookingResponseDto actualBooking = bookingService.addNew(bookingRequestDto, 2L);

        verify(bookingRepository).save(bookingArgumentCaptor.capture());
        final Booking savedBooking = bookingArgumentCaptor.getValue();

        assertEquals(savedBooking.getItem().getId(), item.getId());
        assertEquals(savedBooking.getBooker().getId(), booker.getId());
        assertEquals(savedBooking.getStart(), bookingRequestDto.getStart());
        assertEquals(savedBooking.getEnd(), bookingRequestDto.getEnd());
    }

    @Test
    public void addNew_whenUserFoundAndItemNotAvailable_thenBadRequestExceptionThrown() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final BookingRequestDto bookingRequestDto = new BookingRequestDto();
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");

        item.setId(1L);
        item.setAvailable(false);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);
        bookingRequestDto.setStart(currentTime.plusDays(1));
        bookingRequestDto.setEnd(currentTime.plusDays(2));
        bookingRequestDto.setItemId(item.getId());

        when(itemService.getById(1L)).thenReturn(itemMapper.toItemDto(item));

        assertThrows(BadRequestException.class, () -> bookingService.addNew(bookingRequestDto, 2L));
        verify(bookingRepository, Mockito.never())
                .save(any(Booking.class));
    }

    @Test
    public void approve_whenPermissionOkAndBookingInWaitingStatus_thenReturnApprovedBooking() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");
        final Booking booking = new Booking();

        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(currentTime.plusDays(1));
        booking.setEnd(currentTime.plusDays(2));

        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        final BookingResponseDto actualBooking = bookingService.approve(1L, 1L, true);

        assertEquals(actualBooking.getId(), booking.getId());
        assertEquals(actualBooking.getItem().getId(), item.getId());
        assertEquals(actualBooking.getStatus(), BookingStatus.APPROVED);
    }

    @Test
    public void approve_whenPermissionOkAndBookingNotInWaitingStatus_thenBadRequestExceptionThrown() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");
        final Booking booking = new Booking();

        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.REJECTED);
        booking.setStart(currentTime.plusDays(1));
        booking.setEnd(currentTime.plusDays(2));

        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(BadRequestException.class, () -> bookingService.approve(1L, 1L, true));
        assertEquals(booking.getStatus(), BookingStatus.REJECTED);
    }

    @Test
    public void getToUserById_whenUserIsOwner_thenNotExceptionThrown() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");
        final Booking booking = new Booking();

        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(currentTime.plusDays(1));
        booking.setEnd(currentTime.plusDays(2));

        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        BookingResponseDto bookings = bookingService.getToUserById(1L, 1L);
    }

    @Test
    public void getToUserById_whenUserIsNotOwnerAndNotBooker_thenBadRequestExceptionThrown() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);
        final LocalDateTime currentTime = LocalDateTime.now();
        final Item item = ObjectsFactory.newItem("name", "");
        final User owner = ObjectsFactory.newUser("emailowner", "nameowner");
        final User booker = ObjectsFactory.newUser("emailbooker", "namebooker");
        final Booking booking = new Booking();

        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setStart(currentTime.plusDays(1));
        booking.setEnd(currentTime.plusDays(2));

        item.setId(1L);
        item.setAvailable(true);
        item.setOwner(owner);
        owner.setId(1L);
        booker.setId(2L);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        assertThrows(BadRequestException.class, () -> bookingService.getToUserById(1L, 3L));
    }

    @Test
    public void getByItemOwner_whenUserExists_thenCallRepository() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);

        List<BookingResponseDto> bookings = bookingService.getByItemOwner(1L, RequestBookingStateDto.CURRENT);
        verify(bookingRepository, times(1))
                .findAllByItemOwnerWithItemAndBookerEagerly(1L, RequestBookingStateDto.CURRENT);
    }

    @Test
    public void getByBooker_whenUserExists_thenCallRepository() {
        final BookingService bookingService = new BookingServiceImpl(bookingRepository, itemService, userService,
                userMapper, itemMapper, bookingMapper);

        List<BookingResponseDto> bookings = bookingService.getByBooker(1L, RequestBookingStateDto.CURRENT);
        verify(bookingRepository, times(1))
                .findAllByBookerWithItemAndBookerEagerly(1L, RequestBookingStateDto.CURRENT);
    }
}