package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepositoryCustom {

    List<Booking> findByItemsOwner(Long ownerId, RequestBookingStateDto state);

    List<Booking> findAllByBooker(Long bookerId, RequestBookingStateDto state);

    Optional<Booking> findByLastOrNextByItem(Long itemId, int direct);
}

