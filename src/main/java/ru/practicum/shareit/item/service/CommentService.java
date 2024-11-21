package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentService {

    Comment save(Comment comment);

    List<Comment> getItemComments(Item item);

    List<Comment> getItemsComments(List<Item> items);

}
