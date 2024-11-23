package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long>, BookingRepositoryCustom {

    @Query("select b from Booking b inner join fetch b.item i inner join fetch b.booker bk" +
            "inner join fetch i.owner o where b.id = :id")
    Optional<Booking> findByIdWithItemAndBookerAndOwnerEagerly(@Param("id")  Long id);

    List<Booking> findAllByItemIdInAndStatus(Set<Long> itemIds, BookingStatus status);

    boolean existsByItemAndBookerAndStatusAndEndBefore(Item item, User user, BookingStatus status,
                                                       LocalDateTime created);
}

