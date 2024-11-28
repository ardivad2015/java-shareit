package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BookingApproveDto {

    private Long bookingId;
    private Long userId;
    private Boolean approved;
}
