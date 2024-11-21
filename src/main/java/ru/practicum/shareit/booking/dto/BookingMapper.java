package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookingMapper {

    Booking toBooking(BookingRequestDto bookingRequestDto);

    BookingResponseDto toBookingResponseDto(Booking booking);

}
