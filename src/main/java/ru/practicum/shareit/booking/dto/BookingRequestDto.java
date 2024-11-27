package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.dto.validation.TimePeriod;

import java.time.LocalDateTime;

@Getter
@Setter
@TimePeriod
public class BookingRequestDto {

    @NotNull
    @Positive
    private Long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
}
