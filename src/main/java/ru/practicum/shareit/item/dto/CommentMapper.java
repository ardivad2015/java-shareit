package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    Comment toComment(CommentDto commentDto);

    @Mapping(target = "authorName", source = "author.name")
    CommentDto toCommentDto(Comment comment);
}
