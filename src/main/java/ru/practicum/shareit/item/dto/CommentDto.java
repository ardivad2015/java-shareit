package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.internal.bytebuddy.asm.Advice;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

import java.time.LocalDateTime;

@Setter
@Getter
public class CommentDto {

    private Long id;
    private Long itemId;
    @NotBlank
    private String text;
    private String authorName;
    private LocalDateTime created;
}