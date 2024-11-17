package ru.practicum.shareit.booking.service;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.error.ErrorResponse;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMetException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public Booking save(Booking booking) {
        checkBeforeSave(booking);
        return bookingRepository.save(booking);
    }

    private void checkBeforeSave(Booking newBooking) {
        final List<String> errorList = new ArrayList<>();
        Item item = newBooking.getItem();
        if (Objects.nonNull(item) && !item.getAvailable()) {
            errorList.add(String.format("Товар с id %d недоступен для бронирования", item.getId()));
        }
        if (!errorList.isEmpty()) {
            throw new BadRequestException(new ErrorResponse(errorList));
        }
    }
}
