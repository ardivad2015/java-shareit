package ru.practicum.shareit.booking.repository;

import com.querydsl.core.BooleanBuilder;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.user.model.QUser;

import java.time.LocalDateTime;

public class BookingRepositoryCustomImpl extends QuerydslRepositorySupport implements BookingRepositoryCustom {

    public BookingRepositoryCustomImpl() {
        super(Booking.class);
    }

    public Iterable<Booking> findByItemsOwner(Long ownerId, RequestBookingStateDto state) {

        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QItem.item.owner.id.eq(ownerId));

        final LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case PAST -> booleanBuilder.and(QBooking.booking.end.before(currentTime));
            case FUTURE -> booleanBuilder.and(QBooking.booking.start.after(currentTime));
            case CURRENT -> booleanBuilder.and(QBooking.booking.end.after(currentTime))
                    .and(QBooking.booking.start.before(currentTime));
            case REJECTED -> booleanBuilder.and(QBooking.booking.status.eq(BookingStatus.REJECTED));
            case WAITING -> booleanBuilder.and(QBooking.booking.status.eq(BookingStatus.WAITING));
        }

        return from(QBooking.booking)
                .innerJoin(QBooking.booking.item, QItem.item).fetchJoin()
                .innerJoin(QBooking.booking.booker, QUser.user).fetchJoin()
                .where(booleanBuilder.getValue())
                .stream().toList();
    }
}

