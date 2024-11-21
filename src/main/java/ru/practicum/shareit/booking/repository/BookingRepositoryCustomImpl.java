package ru.practicum.shareit.booking.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.model.QBooking;
import ru.practicum.shareit.item.model.QItem;
import ru.practicum.shareit.user.model.QUser;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class BookingRepositoryCustomImpl extends QuerydslRepositorySupport implements BookingRepositoryCustom {

    public BookingRepositoryCustomImpl() {
        super(Booking.class);
    }

    @Override
    public Optional<Booking> findByLastOrNextByItem(Long itemId, int direct) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        final LocalDateTime currentTime = LocalDateTime.now();
        final QBooking booking = QBooking.booking;
        booleanBuilder.and(booking.item.id.eq(itemId))
                .and(booking.status.eq(BookingStatus.APPROVED));
        OrderSpecifier<LocalDateTime> orderSpecifier;
        if (direct == -1) {
            orderSpecifier = booking.start.desc();
            booleanBuilder.and(booking.start.before(currentTime));
        } else {
            orderSpecifier = booking.start.asc();
            booleanBuilder.and(booking.start.after(currentTime));
        };
        return from(booking)
                .where(booleanBuilder.getValue())
                .orderBy(orderSpecifier)
                .limit(1).stream().findFirst();
    }

    @Override
    public List<Booking> findAllByBooker(Long bookerId, RequestBookingStateDto state) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QBooking.booking.booker.id.eq(bookerId));
        buildByState(booleanBuilder, state);

        return from(QBooking.booking)
                .innerJoin(QBooking.booking.item, QItem.item).fetchJoin()
                .innerJoin(QBooking.booking.booker, QUser.user).fetchJoin()
                .where(booleanBuilder.getValue()).fetch();
    }

    @Override
    public List<Booking> findByItemsOwner(Long ownerId, RequestBookingStateDto state) {
        final BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QItem.item.owner.id.eq(ownerId));
        buildByState(booleanBuilder, state);
        return from(QBooking.booking)
                .innerJoin(QBooking.booking.item, QItem.item).fetchJoin()
                .innerJoin(QBooking.booking.booker, QUser.user).fetchJoin()
                .where(booleanBuilder.getValue()).fetch();

    }

    private void buildByState(BooleanBuilder booleanBuilder, RequestBookingStateDto state) {
        final LocalDateTime currentTime = LocalDateTime.now();
        switch (state) {
            case PAST -> booleanBuilder.and(QBooking.booking.end.before(currentTime));
            case FUTURE -> booleanBuilder.and(QBooking.booking.start.after(currentTime));
            case CURRENT -> booleanBuilder.and(QBooking.booking.end.after(currentTime))
                    .and(QBooking.booking.start.before(currentTime));
            case REJECTED -> booleanBuilder.and(QBooking.booking.status.eq(BookingStatus.REJECTED));
            case WAITING -> booleanBuilder.and(QBooking.booking.status.eq(BookingStatus.WAITING));
        }
    }
}

