package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface BookingService {

    BookingResponseDto getToUserById(Long bookingId, Long userId);

    List<BookingResponseDto> getByBooker(Long userId, RequestBookingStateDto state);

    List<BookingResponseDto> getByItemOwner(Long userId, RequestBookingStateDto state);

    BookingResponseDto addNew(BookingRequestDto booking, Long userId);

    BookingResponseDto approve(Long bookingId, Long userId, Boolean approved);
}
