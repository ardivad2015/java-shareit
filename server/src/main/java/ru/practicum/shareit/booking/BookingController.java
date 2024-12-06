package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingResponseDto getById(@PathVariable("id") Long bookingId,
                                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.getToUserById(bookingId, userId);
    }

    @GetMapping
    public List<BookingResponseDto> getByBooker(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL")
                                                    RequestBookingStateDto state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByItemOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     RequestBookingStateDto state) {
        return bookingService.getByItemOwner(userId, state);
    }

    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                            @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addNew(bookingRequestDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto approve(@PathVariable("id") Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }
}
