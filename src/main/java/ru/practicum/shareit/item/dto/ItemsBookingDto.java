package ru.practicum.shareit.item.dto;

import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Setter
public class ItemsBookingDto {

    private LocalDateTime start;
    private LocalDateTime end;
}
