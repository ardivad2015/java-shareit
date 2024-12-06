package ru.practicum.shareit.itemrequest.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestDto {

    private Long id;
    @NotNull
    private String description;
    private LocalDateTime created;
}