package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentDto {

    private Long id;
    private Long itemId;
    private String text;
    private String authorName;
    private LocalDateTime created;
}