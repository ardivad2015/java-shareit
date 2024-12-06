package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingPeriodDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.InsufficientPermissionException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentMapper commentMapper;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;

    @Override
    public ItemDto getById(Long id) {
        return itemMapper.toItemDto(findById(id));
    }

    @Override
    public ItemEnhancedDto getByIdWithComments(Long id) {
        return makeItemEnhancedDto(findById(id), getItemsComments(Set.of(id)), Collections.emptyList());
    }

    @Override
    public void existsById(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new NotFoundException(String.format("Вещь с id = %d не найдена", id));
        }
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameLikeOrDescriptionLikeAndAvailable(text).stream()
                .map(itemMapper::toItemDto).toList();
    }

    @Override
    public List<ItemEnhancedDto> getByOwner(Long userId) {
        userService.existsById(userId);

        final Map<Long, Item> items = itemRepository.findAllByOwnerId(userId).stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));

        final Map<Long, List<CommentDto>> comments = getItemsComments(items.keySet())
                .stream()
                .collect(Collectors.groupingBy(CommentDto::getItemId));

        final Map<Long, List<BookingResponseDto>> bookings = bookingRepository.findAllByItemIdInAndStatus(items.keySet(),
                        BookingStatus.APPROVED).stream()
                .map(bookingMapper::toBookingResponseDto)
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId()));

        return items.values().stream()
                .map(item -> makeItemEnhancedDto(item, comments.getOrDefault(item.getId(), Collections.emptyList()),
                        bookings.getOrDefault(item.getId(), Collections.emptyList()))
                )
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public NewItemDto addNewItem(NewItemDto newItemDto, Long ownerId) {
        final User owner = userMapper.toUser(userService.getById(ownerId));
        final Item item = itemMapper.toItem(newItemDto);
        final Long requestId = newItemDto.getRequestId();

        if (Objects.nonNull(requestId)) {
            final ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() ->
                    new NotFoundException(String.format("Запрос с id = %d не найден", requestId)));
            item.setItemRequest(itemRequest);
        }
        item.setOwner(owner);

        return itemMapper.toNewItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(ItemDto updatedItem, Long userId) {
        userService.existsById(userId);

        final Item item = findById(updatedItem.getId());
        checkPermission(item, userId);

        final String name = updatedItem.getName();

        if (Objects.nonNull(name) && !name.isBlank()) {
            item.setName(name);
        }

        final String description = updatedItem.getDescription();

        if (Objects.nonNull(description) && !description.isBlank()) {
            item.setDescription(description);
        }

        final Boolean available = updatedItem.getAvailable();

        if (Objects.nonNull(available)) {
            item.setAvailable(available);
        }
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void checkPermission(Item item, Long userId) {
        final Optional<Long> optOwnerId = Optional.ofNullable(item.getOwner()).map(User::getId);
        final boolean isOwner = optOwnerId.map(userId::equals).orElse(false);

        if (!isOwner) {
            throw new InsufficientPermissionException("Пользователь не является владельцем вещи");
        }
    }

    @Override
    public List<CommentDto> getItemsComments(Set<Long> itemSet) {
        return commentRepository.findAllByItemIdInWithAuthorEagerly(itemSet).stream()
                .map(commentMapper::toCommentDto).toList();
    }

    @Override
    @Transactional
    public CommentDto addNewComment(CommentDto commentDto, Long itemId, Long userId) {
        final UserDto author = userService.getById(userId);
        final Item item = findById(itemId);
        final LocalDateTime created = LocalDateTime.now();

        if (!bookingRepository.existsByItemIdAndBookerIdAndStatusAndEndBefore(itemId, userId, BookingStatus.APPROVED,
                created)) {
            throw BadRequestException.simpleBadRequestException("У пользователя не было подтверждённого " +
                    "бронирования вещи");
        }

        Comment comment = commentMapper.toComment(commentDto);
        comment.setCreated(created);
        comment.setAuthor(userMapper.toUser(author));
        comment.setItem(item);
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private Item findById(Long id) {
        return itemRepository.findById(id).orElseThrow(() ->
                new NotFoundException(String.format("Вещь с id = %d не найдена", id)));
    }

    private ItemEnhancedDto makeItemEnhancedDto(Item item, List<CommentDto> comments,
                                                List<BookingResponseDto> bookings) {
        final LocalDateTime currentDateLime = LocalDateTime.now();

        final Optional<BookingPeriodDto> optLastBooking = bookings.stream()
                .sorted(Comparator.comparing(BookingResponseDto::getStart).reversed())
                .filter(booking -> booking.getStart().isBefore(currentDateLime)
                        || booking.getStart().isEqual(currentDateLime))
                .limit(1)
                .map(booking -> new BookingPeriodDto(booking.getStart(), booking.getEnd()))
                .findFirst();

        final Optional<BookingPeriodDto> optNextBooking = bookings.stream()
                .sorted(Comparator.comparing(BookingResponseDto::getStart))
                .filter(booking -> booking.getStart().isAfter(currentDateLime))
                .limit(1)
                .map(booking -> new BookingPeriodDto(booking.getStart(), booking.getEnd()))
                .findFirst();

        final ItemEnhancedDto itemEnhancedDto = itemMapper.toItemEnhancedDto(item);

        itemEnhancedDto.setComments(comments);
        optLastBooking.ifPresent(itemEnhancedDto::setLastBooking);
        optNextBooking.ifPresent(itemEnhancedDto::setNextBooking);
        return itemEnhancedDto;
    }
}
