package ru.practicum.shareit.booking.service;

import com.querydsl.core.types.PredicateOperation;
import com.querydsl.core.types.dsl.BooleanExpression;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.server.DelegatingServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional(readOnly = true)
    public Booking getToUserById(Long bookingId, Long userId) {
        final Booking booking = getById(bookingId);
        checkBeforeGetById(booking, userId);
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getByItemOwner(Long userId, RequestBookingStateDto state) {
        return bookingRepository.findAllByItemOwnerWithItemAndBookerEagerly(userId, state);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getByBooker(Long userId, RequestBookingStateDto state) {
        return bookingRepository.findAllByBookerWithItemAndBookerEagerly(userId, state);
    }

    @Override
    @Transactional
    public Booking save(Booking booking) {
        checkBeforeSave(booking);
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public Booking approve(Long bookingId, Long userId, Boolean approved) {
        final Booking booking = getById(bookingId);
        checkBeforeApprove(booking, userId);
        BookingStatus status = approved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(status);
        return booking;
    }

    @Override
    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return bookingRepository.findByIdWithItemAndBookerAndOwnerEagerly(id).orElseThrow(() ->
                new NotFoundException(String.format("Бронирование с id = %d не найдено", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getByItems(Set<Long> itemIds) {
        return bookingRepository.findAllByItemIdInAndStatus(itemIds, BookingStatus.APPROVED);
    }

    private void checkBeforeSave(Booking newBooking) {
        final List<String> errorList = new ArrayList<>();
        final Item item = newBooking.getItem();
        if (Objects.nonNull(item) && !item.getAvailable()) {
            errorList.add(String.format("Товар с id %d недоступен для бронирования", item.getId()));
        }
        if (!errorList.isEmpty()) {
            throw new BadRequestException(new ErrorResponse(errorList));
        }
    }

    private void checkBeforeApprove(Booking booking, Long userId) {
        final List<String> errorList = new ArrayList<>();
        //Почему-то в этом тесте в постмэне на эту ошибку код 400.
        try {
            userService.ExistsById(userId);
        } catch (NotFoundException e) {
            errorList.add(e.getMessage());
            throw new BadRequestException(new ErrorResponse(errorList));
        }
        final Item item = booking.getItem();
        if (Objects.isNull(item)) {
                throw new NotFoundException("В бронировании не указана вещь.");
        }
        if (!itemService.userIsOwner(item, userId)) {
            errorList.add("Пользователь не является владельцем вещи");
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            errorList.add("Бронирование не находится в статусе ожидания подтверждения.");
        }
        if (!errorList.isEmpty()) {
            throw new BadRequestException(new ErrorResponse(errorList));
        }
    }

    private void checkBeforeGetById(Booking booking, Long userId) {
        final List<String> errorList = new ArrayList<>();
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
            errorList.add("Пользователь должен быть либо владельцем вещи, либо автором бронирования");
        }
        if (!errorList.isEmpty()) {
            throw new BadRequestException(new ErrorResponse(errorList));
        }
    }

}
