package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto saveNewUser(@Valid @RequestHeader("X-Sharer-User-Id") Long userId,
                               @Valid @RequestBody ItemDto itemDto) {
        itemDto.setOwner(userId);
        return itemService.saveItem(itemDto);
    }
}
