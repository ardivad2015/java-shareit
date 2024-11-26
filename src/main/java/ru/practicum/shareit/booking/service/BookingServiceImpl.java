package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.InsufficientPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    public BookingResponseDto getToUserById(Long bookingId, Long userId) {
        final Booking booking = getById(bookingId);
        checkPermission(booking, userId);
        return bookingMapper.toBookingResponseDto(booking);
    }

    @Override
    public List<BookingResponseDto> getByItemOwner(Long userId, RequestBookingStateDto state) {
        userService.ExistsById(userId);
        return bookingRepository.findAllByItemOwnerWithItemAndBookerEagerly(userId, state).stream()
                .map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    public List<BookingResponseDto> getByBooker(Long userId, RequestBookingStateDto state) {
        userService.ExistsById(userId);
        return bookingRepository.findAllByBookerWithItemAndBookerEagerly(userId, state).stream()
                .map(bookingMapper::toBookingResponseDto).toList();
    }

    @Override
    @Transactional
    public BookingResponseDto addNew(BookingRequestDto bookingDto, Long userId) {
        final User booker = userMapper.toUser(userService.getById(userId));
        final Item item = itemMapper.toItem(itemService.getById(bookingDto.getItemId()));

        if (!item.getAvailable()) {
            throw BadRequestException.simpleBadRequestException(String.format("Товар с id %d недоступен для " +
                            "бронирования", item.getId()));
        }

        Booking booking = bookingMapper.toBooking(bookingDto);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingResponseDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponseDto approve(Long bookingId, Long userId, Boolean approved) {
        final Booking booking = getById(bookingId);
        // в постмэн тестах в этом эндпоинте для несуществующего пользователя нужна ошибка не 404, а 400.
        try {
            userService.ExistsById(userId);
        } catch (NotFoundException e) {
            throw BadRequestException.simpleBadRequestException(e.getMessage());
        }

        itemService.checkPermission(booking.getItem(), userId);

        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw BadRequestException.simpleBadRequestException("Бронирование не находится в статусе ожидания " +
                    "подтверждения.");
        }

        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        return bookingMapper.toBookingResponseDto(booking);
    }

    private void checkPermission(Booking booking, Long userId) {
        userService.ExistsById(userId);

        final Item item = booking.getItem();

        if (Objects.isNull(item)) {
            throw new NotFoundException("В бронировании не указана вещь.");
        }

        final User owner = item.getOwner();

        if (Objects.isNull(owner)) {
            throw new NotFoundException("Владелец вещи в бронировании не указан.");
        }

        final User booker = booking.getBooker();

        if (Objects.isNull(booker)) {
            throw new NotFoundException("Автор бронирования не указан.");
        }

        if (!owner.getId().equals(userId) && !booker.getId().equals(userId)) {
            throw BadRequestException.simpleBadRequestException("Пользователь должен быть либо владельцем вещи," +
                    " либо автором бронирования");
        }
    }

    private Booking getById(Long id) {
        return bookingRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование с id = %d не найдено", id)));
    }

}
