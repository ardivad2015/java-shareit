package ru.practicum.shareit.request.dto;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.time.LocalDateTime;

@Getter
@Setter
public class ItemRequestDto {

    private Long id;
    @NotNull
    private String description;
    private LocalDateTime created;
}