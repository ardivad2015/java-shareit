package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping("/{id}")
    public ItemEnhancedDto getById(@PathVariable("id") Long itemId) {
        return  itemService.getByIdWithComments(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        return itemService.searchItems(text);
    }

    @GetMapping
    public List<ItemEnhancedDto> getByOwner(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByOwner(userId);
    }

    @PostMapping
    public NewItemDto addNewItem(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestBody NewItemDto itemDto) {
        return itemService.addNewItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@PathVariable("id") Long itemId, @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        itemDto.setId(itemId);
        return itemService.updateItem(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addNewComment(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("id") Long itemId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.addNewComment(commentDto, itemId, userId);
    }


}
