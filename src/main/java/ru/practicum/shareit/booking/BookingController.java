package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingMapper bookingMapper;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final UserService userService;

    @GetMapping("/{id}")
    public BookingResponseDto getById(@PathVariable("id") @Positive Long bookingId,
                                       @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        userService.ExistsById(userId);
        return bookingMapper.toBookingResponseDto(bookingService.getToUserById(bookingId, userId));
    }

    @GetMapping
    public List<BookingResponseDto> getByBooker(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL")
                                                    @NotNull
                                                    RequestBookingStateDto state) {
        userService.ExistsById(userId);
        return bookingService.getByBooker(userId, state).stream()
                .map(bookingMapper::toBookingResponseDto).toList();
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByItemOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     @NotNull RequestBookingStateDto state) {
        userService.ExistsById(userId);
        return bookingService.getByItemOwner(userId, state).stream()
                .map(bookingMapper::toBookingResponseDto).toList();
    }

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

    @PatchMapping("/{id}")
    public BookingResponseDto approve(@PathVariable("id") @Positive Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @RequestParam @NotNull Boolean approved) {
        final Booking booking = bookingService.approve(bookingId, userId, approved);
        return bookingMapper.toBookingResponseDto(booking);
    }
}
