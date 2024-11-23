package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface CommentService {

    Comment save(Comment comment);

    List<Comment> getByItems(Set<Long> itemIds);
}
