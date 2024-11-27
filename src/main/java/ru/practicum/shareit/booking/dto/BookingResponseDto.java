package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingResponseDto {

    private Long id;
    private IdBasedEntityDto item;
    private IdBasedEntityDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
}
