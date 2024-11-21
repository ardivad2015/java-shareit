package ru.practicum.shareit.item.repository;

import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("select c from Comment c inner join fetch c.author where c.item = :item")
    List<Comment> findByItemWithAuthors(@Param("item") Item item);

    @Query("select c from Comment c inner join fetch c.author where c.item in :items")
    List<Comment> findByItemsWithAuthors(@Param("items") List<Item> items);
}
