package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum RequestBookingStateDto {
    ALL, CURRENT, PAST, FUTURE, WAITING, REJECTED;

    public static Optional<RequestBookingStateDto> from(String stringState) {
        for (RequestBookingStateDto state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
