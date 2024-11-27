package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingApproveDto {

    private Long bookingId;
    private Long userId;
    private Boolean approved;

    public BookingApproveDto(Long bookingId, Long userId, Boolean approved) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.approved = approved;
    }
}
