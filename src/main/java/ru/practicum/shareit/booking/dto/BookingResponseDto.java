package ru.practicum.shareit.booking.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.DynamicUpdate;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.time.Instant;
import java.time.LocalDate;
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
