package ru.practicum.shareit.item;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.hibernate.cache.spi.support.AbstractReadWriteAccess;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingPeriodDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.CommentService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/items")
public class ItemController {

    private final CommentService commentService;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @GetMapping("/{id}")
    public ItemEnhancedDto getById(@PathVariable("id") @Positive Long itemId) {
        Item item = itemService.getById(itemId);
        List<Comment> comments = commentService.getByItems(Set.of(itemId));
        return  makeItemEnhancedDto(item, comments, Collections.emptyList());
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam @NotNull String text) {
        return itemService.searchItems(text).stream()
                .map(itemMapper::toItemDto)
                .toList();
    }

    @GetMapping
    public List<ItemEnhancedDto> getByOwner(@Valid @RequestHeader("X-Sharer-User-Id") @Positive Long userId) {
        userService.ExistsById(userId);
        Map<Long, Item> items = itemService.getByOwner(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<Comment>> comments = commentService.getByItems(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));
        Map<Long, List<Booking>> bookings = bookingService.getByItems(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));
        return items.values().stream()
                .map(item -> makeItemEnhancedDto(item, comments.getOrDefault(item.getId(), Collections.emptyList()),
                            bookings.getOrDefault(item.getId(), Collections.emptyList()))
                )
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        final Item item = itemMapper.toItem(itemDto);
        item.setOwner(userService.getById(userId));
        return itemMapper.toItemDto(itemService.save(item));
    }

    @PatchMapping("/{id}")
    public ItemDto update(@PathVariable("id") @Positive Long itemId,
                          @RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                          @RequestBody ItemDto itemDto) {
        userService.ExistsById(userId);
        Item item = itemMapper.toItem(itemDto);
        item.setId(itemId);
        return itemMapper.toItemDto(itemService.update(item, userId));
    }

    @PostMapping("/{id}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") @Positive Long userId,
                                    @PathVariable("id") @Positive Long itemId,
                                    @Valid @RequestBody CommentDto commentDto) {
        Comment comment = commentMapper.toComment(commentDto);
        User author = userService.getById(userId);
        Item item = itemService.getById(itemId);
        comment.setAuthor(author);
        comment.setItem(item);
        comment.setCreated(LocalDateTime.now());
        return commentMapper.toCommentDto(commentService.save(comment));
    }

    private ItemEnhancedDto makeItemEnhancedDto(Item item, List<Comment> comments, List<Booking> bookings) {
        List<CommentDto> commentDtos = comments
                .stream()
                .map(commentMapper::toCommentDto)
                .toList();

        LocalDateTime currentDateLime = LocalDateTime.now();

        Optional<BookingPeriodDto> optLastBooking = bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(currentDateLime)
                        || booking.getStart().isEqual(currentDateLime))
                .limit(1)
                .map(booking -> new BookingPeriodDto(booking.getStart(), booking.getEnd()))
                .findFirst();

        Optional<BookingPeriodDto> optNextBooking = bookings.stream()
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStart().isAfter(currentDateLime))
                .limit(1)
                .map(booking -> new BookingPeriodDto(booking.getStart(), booking.getEnd()))
                .findFirst();

        ItemEnhancedDto itemEnhancedDto = itemMapper.toItemEnhancedDto(item);
        itemEnhancedDto.setComments(commentDtos);
        optLastBooking.ifPresent(itemEnhancedDto::setLastBooking);
        optNextBooking.ifPresent(itemEnhancedDto::setNextBooking);
        return itemEnhancedDto;
    }
}
