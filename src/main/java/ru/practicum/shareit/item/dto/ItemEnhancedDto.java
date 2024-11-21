package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemEnhancedDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemsBookingDto lastBooking;
    private ItemsBookingDto nextBooking;
    private List<CommentDto> comments = new ArrayList<>();
}
