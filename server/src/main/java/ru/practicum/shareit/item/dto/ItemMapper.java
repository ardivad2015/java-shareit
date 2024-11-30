package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.dto.IdBasedEntityDto;

@Mapper(componentModel = "spring")
public interface ItemMapper {

        Item toItem(ItemDto itemDto);

        ItemDto toItemDto(Item item);

        ItemEnhancedDto toItemEnhancedDto(Item item);

        IdBasedEntityDto toIdBasedDto(Item item);
}
