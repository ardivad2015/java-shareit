package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemEnhancedDto getById(@PathVariable("id") @Positive Long itemId) {
        return  itemService.getByIdWithComments(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotNull String text) {
        return itemService.searchItems(text);
    }

    @GetMapping
    public List<ItemEnhancedDto> getByOwner(@Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {

        return itemService.getByOwner(userId);
    }

    @PostMapping
    public ItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") @Positive Long itemId,
                          @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                    @PathVariable("id") @Positive Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addNewComment(commentDto, itemId, userId);
    }


}
