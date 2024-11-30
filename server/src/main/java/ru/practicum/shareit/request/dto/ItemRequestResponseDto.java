package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ItemRequestResponseDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private IdBasedEntityDto author;
    private List<IdBasedEntityDto> items;
}