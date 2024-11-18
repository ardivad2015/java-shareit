package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.boot.Banner;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
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
    public BookingResponseDto findById(@PathVariable("id") @Positive Long bookingId,
                                      @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return bookingMapper.toBookingResponseDto(bookingService.findById(bookingId, userId));
    }
    @GetMapping("/owner")
    public List<BookingResponseDto> findByItemsOwner(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                                    @RequestParam(name = "state", defaultValue = "ALL")
                                                    @NotNull RequestBookingStateDto state) {
        return bookingMapper.toListBookingResponseDto(bookingService.findByItemsOwner(userId, state));
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
        Booking booking = bookingService.approve(new BookingApproveDto(bookingId, userId, approved));
        return bookingMapper.toBookingResponseDto(booking);
    }
}
