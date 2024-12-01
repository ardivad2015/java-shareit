package ru.practicum.shareit.request.service;


import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto addNewItemRequest(ItemRequestDto itemRequestDto, Long userId);

    List<ItemRequestResponseDto> getByAuthor(Long userId);
}
