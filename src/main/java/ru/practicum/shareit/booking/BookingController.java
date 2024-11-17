package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    @PostMapping
    public BookingResponseDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        Booking booking = bookingMapper.toBooking(bookingRequestDto);
        Item item = itemService.getById(bookingRequestDto.getItemId());
        User user = userService.getById(userId);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingResponseDto(bookingService.save(booking));
    }
}
