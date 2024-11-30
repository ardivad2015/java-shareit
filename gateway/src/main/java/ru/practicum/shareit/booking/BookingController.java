package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Validated
public class BookingController {

    private final BookingClient bookingClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@PathVariable("id") @Positive Long bookingId,
                                     @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return bookingClient.getBooking(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL")
                                                    @NotNull
                                                    RequestBookingStateDto state) {
        return bookingClient.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getByItemOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     @NotNull RequestBookingStateDto state) {
        return bookingClient.getByItemOwner(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> addNewBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingClient.addNewBooking(userId, bookingRequestDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> approveBooking(@PathVariable("id") @Positive Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @RequestParam @NotNull Boolean approved) {
        return bookingClient.approveBooking(bookingId, userId, approved);
    }
}
