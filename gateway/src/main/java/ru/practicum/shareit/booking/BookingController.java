package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.service.BookingService;

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
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getByBooker(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL")
                                                    @NotNull
                                                    RequestBookingStateDto state) {
        RequestBookingStateDto state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        return bookingClient.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingResponseDto> getByItemOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                     @RequestParam(name = "state", defaultValue = "ALL")
                                                     @NotNull RequestBookingStateDto state) {
        return bookingService.getByItemOwner(userId, state);
    }

    @PostMapping
    public BookingResponseDto addNewBooking(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                     @Valid @RequestBody BookingRequestDto bookingRequestDto) {
        return bookingService.addNew(bookingRequestDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingResponseDto approve(@PathVariable("id") @Positive Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                      @RequestParam @NotNull Boolean approved) {
        return bookingService.approve(bookingId, userId, approved);
    }
}
