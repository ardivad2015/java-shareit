package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface BookingService {

    Booking findById(Long bookingId, Long userId);

    List<Booking> findAllByBooker(Long userId, RequestBookingStateDto state);

    List<Booking> findByItemsOwner(Long userId, RequestBookingStateDto state);

    Booking save(Booking booking);

    Booking approve(BookingApproveDto bookingApproveDto);
}
