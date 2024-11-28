package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;

import java.util.List;

public interface BookingService {

    BookingResponseDto getToUserById(Long bookingId, Long userId);

    List<BookingResponseDto> getByBooker(Long userId, RequestBookingStateDto state);

    List<BookingResponseDto> getByItemOwner(Long userId, RequestBookingStateDto state);

    BookingResponseDto addNew(BookingRequestDto booking, Long userId);

    BookingResponseDto approve(Long bookingId, Long userId, Boolean approved);
}
