package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.ObjectsFactory;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryCustomTest {

    @Autowired
    BookingRepository bookingRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ItemRepository itemRepository;

    @Test
    void findAllByItemOwnerWithItemAndBookerEagerly() {
        final User owner = userRepository.save(ObjectsFactory.newUser("email", "owner"));
        final User owner2 = userRepository.save(ObjectsFactory.newUser("email1", "owner2"));
        final User booker = userRepository.save(ObjectsFactory.newUser("email2", "booker"));
        final User booker2 = userRepository.save(ObjectsFactory.newUser("email3", "booker2"));
        final Item item = ObjectsFactory.newItem("item_name", "item_description");
        final Item item2 = ObjectsFactory.newItem("item_name2", "item_description2");
        final Booking booking = new Booking();
        final Booking booking2 = new Booking();

        item.setOwner(owner);
        item.setAvailable(true);
        item2.setOwner(owner2);
        item2.setAvailable(true);

        final Item savedItem = itemRepository.save(item);
        final Item savedItem2 = itemRepository.save(item2);

        booking.setItem(savedItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        booking2.setItem(savedItem2);
        booking2.setBooker(booker2);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAllByItemOwnerWithItemAndBookerEagerly(owner.getId(),
                RequestBookingStateDto.ALL);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.getFirst().getItem().getName(), item.getName());
    }

    @Test
    void findAllByBookerWithItemAndBookerEagerly() {
        final User owner = userRepository.save(ObjectsFactory.newUser("email", "owner"));
        final User owner2 = userRepository.save(ObjectsFactory.newUser("email1", "owner2"));
        final User booker = userRepository.save(ObjectsFactory.newUser("email2", "booker"));
        final User booker2 = userRepository.save(ObjectsFactory.newUser("email3", "booker2"));
        final Item item = ObjectsFactory.newItem("item_name", "item_description");
        final Item item2 = ObjectsFactory.newItem("item_name2", "item_description2");
        final Booking booking = new Booking();
        final Booking booking2 = new Booking();

        item.setOwner(owner);
        item.setAvailable(true);
        item2.setOwner(owner2);
        item2.setAvailable(true);

        final Item savedItem = itemRepository.save(item);
        final Item savedItem2 = itemRepository.save(item2);

        booking.setItem(savedItem);
        booking.setBooker(booker);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        booking2.setItem(savedItem2);
        booking2.setBooker(booker2);
        booking2.setStart(LocalDateTime.now().plusDays(1));
        booking2.setEnd(LocalDateTime.now().plusDays(2));
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

        List<Booking> bookings = bookingRepository.findAllByBookerWithItemAndBookerEagerly(booker2.getId(),
                RequestBookingStateDto.ALL);

        assertEquals(bookings.size(), 1);
        assertEquals(bookings.getFirst().getItem().getName(), item2.getName());
    }
}