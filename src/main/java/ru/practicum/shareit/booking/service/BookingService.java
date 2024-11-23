package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingApproveDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface BookingService {

    Booking getById(Long bookingId);

    Booking getToUserById(Long bookingId, Long userId);

    List<Booking> getByBooker(Long userId, RequestBookingStateDto state);

    List<Booking> getByItemOwner(Long userId, RequestBookingStateDto state);

    Booking save(Booking booking);

    Booking approve(Long bookingId, Long userId, Boolean approved);

    List<Booking> getByItems(Set<Long> itemIds);
}
