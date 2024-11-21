package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final CommentService commentService;
    private final ItemService itemService;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping("/{id}")
    public ItemEnhancedDto getItemInfo(@PathVariable("id") @Positive Long itemId) {
        Item item = itemService.getById(itemId);
        List<CommentDto> comments = commentService.getItemComments(item).stream()
                .map(commentMapper::toCommentDto).toList();
        ItemEnhancedDto itemDto = itemMapper.toItemEnhancedDto(item);
        itemDto.setComments(comments);
        return itemDto;
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotNull String text) {
        return itemService.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @GetMapping
    public List<ItemEnhancedDto> getUsersItems(@Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        List<Item> items =  itemService.getUsersItems(userId);
        List<Comment> comments = commentService.getItemsComments(items);
        List<ItemDto> itemDtos = items.stream()
                .map(item -> {ItemEnhancedDto itemDto = itemMapper.toItemEnhancedDto(item);
                    itemDto.setComments(
comments.stream().filter(comment -> comment.getItem().getId().equals(item.getId()))
        .map(commentMapper::toCommentDto).toList());

                })
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        final Item item = itemMapper.toItem(itemDto);
        final User user = userService.getById(userId);
        item.setOwner(user);
        return itemMapper.toItemDto(itemService.save(item));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable("id") @Positive Long itemId,
                          @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody ItemDto itemDto) {
        Item item = itemMapper.toItem(itemDto);
        item.setId(itemId);
        return itemMapper.toItemDto(itemService.update(item, userId));
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                 @PathVariable("id") @Positive Long itemId, @Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        User author = userService.getById(userId);
        Item item = itemService.getById(itemId);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentService.save(comment));
    }
}
