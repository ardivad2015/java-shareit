package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto addNewItemRequest(ItemRequestDto itemRequestDto, Long userId) {
        final User author = userMapper.toUser(userService.getById(userId));
        final ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);

        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setAuthor(author);

        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestResponseDto> getByAuthor(Long userId) {
        userService.existsById(userId);

        final Map<Long, ItemRequest> itemRequests = itemRequestRepository.findAllByAuthorIdWithAuthorEagerly(userId)
                .stream()
                .collect(Collectors.toMap(ItemRequest::getId, Function.identity()));

        final Map<Long, List<Item>> items = itemRepository.findAllByItemRequestIdIn(itemRequests.keySet())
                .stream()
                .collect(Collectors.groupingBy(item -> item.getItemRequest().getId()));

        return itemRequests.values().stream()
                .sorted(Comparator.comparing(ItemRequest::getCreated).reversed())
                .map(itemRequest -> makeItemRequestResponseDto(itemRequest, items.getOrDefault(itemRequest.getId(),
                                Collections.emptyList())))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAll() {
        return itemRequestRepository.findAll(Sort.by("created").descending()).stream()
                .map(itemRequestMapper::toItemRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestResponseDto getById(Long itemRequestId) {
        final ItemRequest itemRequest = findById(itemRequestId);
        final List<Item> items = itemRepository.findAllByItemRequestId(itemRequestId);
        final ItemRequestResponseDto itemRequestResponseDto = itemRequestMapper.toItemRequestResponseDto(itemRequest);

        itemRequestResponseDto.setItems(items.stream().map(itemMapper::toIdBasedDto).toList());
        return itemRequestResponseDto;
    }

    @Override
    public ItemRequest findById(Long itemRequestId) {
        return itemRequestRepository.findById(itemRequestId).orElseThrow(() ->
                new NotFoundException(String.format("Запрос с id = %d не найден", itemRequestId)));
    }

    private ItemRequestResponseDto makeItemRequestResponseDto(ItemRequest itemRequest, List<Item> items) {
        final ItemRequestResponseDto itemRequestResponseDto = itemRequestMapper.toItemRequestResponseDto(itemRequest);

        itemRequestResponseDto.setItems(items.stream()
                .map(itemMapper::toIdBasedDto)
                .toList());
        return itemRequestResponseDto;
    }
}
