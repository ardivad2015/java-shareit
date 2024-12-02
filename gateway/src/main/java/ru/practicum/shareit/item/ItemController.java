package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable("id") @Positive Long itemId) {
        return  itemClient.getItem(itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@RequestParam @NotNull String text) {
        return itemClient.searchItems(text);
    }

    @GetMapping
    public ResponseEntity<Object> getByOwner(@Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemClient.getItemByOwner(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addNewItem(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Valid @RequestBody NewItemDto newItemDto) {
        return itemClient.addNewItem(userId, newItemDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@PathVariable("id") @Positive Long itemId,
                          @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody ItemDto itemDto) {
        return itemClient.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> addNewComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                    @PathVariable("id") @Positive Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemClient.addNewComment(userId, itemId, commentDto);
    }


}
