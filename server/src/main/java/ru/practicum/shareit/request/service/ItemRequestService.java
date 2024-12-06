package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponseDto> getByAuthor(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestResponseDto getById(Long itemRequestId);

    ItemRequest findById(Long itemRequestId);
}
