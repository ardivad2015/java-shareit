package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getItemComments(Item item) {
        return commentRepository.findByItemWithAuthors(item);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comment> getItemsComments(List<Item> items) {
        return commentRepository.findByItemsWithAuthors(items);
    }

    @Override
    @Transactional
    public Comment save(Comment comment) {
        bookingRepository.findByItemAndBookerAndStatusAndEndBefore(comment.getItem(), comment.getAuthor(),
                BookingStatus.APPROVED, comment.getCreated())
                .orElseThrow(() ->
                        BadRequestException.simpleBadRequestException("У пользователя" +
                                " не было подтверждённого бронирования вещи"));
        return commentRepository.save(comment);
    }
}
