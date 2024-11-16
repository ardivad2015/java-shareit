package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;


import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final ModelMapper modelMapper;

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") @Positive Long itemId) {
        return mapToItemDto(itemService.getById(itemId));
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotNull String text) {
        return itemService.searchItems(text).stream()
                .map(this::mapToItemDto)
                .toList();
    }

    @GetMapping
    public List<ItemDto> getUsersItems(@Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        return itemService.getUsersItems(userId).stream()
                .map(this::mapToItemDto)
                .toList();
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                               @Valid @RequestBody ItemDto itemDto) {
        Item item = mapToItem(itemDto);
        return mapToItemDto(itemService.save(item, userId));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable("id") @Positive Long itemId,
                                 @Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                 @RequestBody ItemDto itemDto) {
        Item item = mapToItem(itemDto);
        item.setId(itemId);
        return mapToItemDto(itemService.update(item, userId));
    }

    public Item mapToItem(ItemDto itemDto) {
        return modelMapper.map(itemDto, Item.class);
    }
    public ItemDto mapToItemDto(Item item) {
        return modelMapper.map(item, ItemDto.class);
    }
}
