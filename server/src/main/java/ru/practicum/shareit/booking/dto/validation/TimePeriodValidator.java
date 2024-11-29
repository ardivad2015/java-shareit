package ru.practicum.shareit.booking.dto.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.util.Objects;

public class TimePeriodValidator implements ConstraintValidator<TimePeriod, BookingRequestDto> {

    @Override
    public void initialize(TimePeriod constraint) {
    }

    @Override
    public boolean isValid(BookingRequestDto bookingRequestDto, ConstraintValidatorContext context) {
        if (Objects.isNull(bookingRequestDto.getEnd()) || Objects.isNull(bookingRequestDto.getStart())) {
            return false;
        }
        return bookingRequestDto.getStart().isBefore(bookingRequestDto.getEnd());
    }
}
