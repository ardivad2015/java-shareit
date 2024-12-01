package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

        Item toItem(ItemDto itemDto);

        Item toItem(NewItemDto newItemDto);

        ItemDto toItemDto(Item item);

        @Mapping(target = "requestId", source = "itemRequest.id")
        NewItemDto toNewItemDto(Item item);

        ItemEnhancedDto toItemEnhancedDto(Item item);

        IdBasedEntityDto toIdBasedDto(Item item);
}
