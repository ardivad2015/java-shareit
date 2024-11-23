package ru.practicum.shareit.booking.repository;

import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepositoryCustom {

    List<Booking> findAllByItemOwnerWithItemAndBookerEagerly(Long ownerId, RequestBookingStateDto state);

    List<Booking> findAllByBookerWithItemAndBookerEagerly(Long bookerId, RequestBookingStateDto state);

}

