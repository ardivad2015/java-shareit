package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;

@Mapper(componentModel = "spring")
public interface ItemMapper {

        Item toItem(ItemDto itemDto);

        ItemDto toItemDto(Item item);

        ItemEnhancedDto toItemEnhancedDto(Item item);
}
