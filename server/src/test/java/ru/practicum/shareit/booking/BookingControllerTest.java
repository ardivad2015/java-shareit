package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.booking.dto.RequestBookingStateDto;
import ru.practicum.shareit.booking.service.BookingService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    ArgumentCaptor<RequestBookingStateDto> requestBookingStateArgumentCaptor;


    @SneakyThrows
    @Test
    void getById() {
        final Long bookingId = 1L;
        final Long userId = 2L;

        mockMvc.perform(get("/bookings/{id}", bookingId)
                .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).getToUserById(bookingId, userId);
    }

    @SneakyThrows
    @Test
    void getByBooker() {
        final Long userId = 1L;
        final String state = "ALL";

        mockMvc.perform(get("/bookings?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).getByBooker(longArgumentCaptor.capture(),requestBookingStateArgumentCaptor.capture());

        final Long actualUserId = longArgumentCaptor.getValue();
        final RequestBookingStateDto actualRequestBookingStateDto = requestBookingStateArgumentCaptor.getValue();

        assertEquals(actualUserId, userId);
        assertEquals(actualRequestBookingStateDto, RequestBookingStateDto.ALL);
    }

    @SneakyThrows
    @Test
    void getByItemOwner() {
        final Long userId = 1L;
        final String state = "ALL";

        mockMvc.perform(get("/bookings/owner?state={state}", state)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).getByItemOwner(longArgumentCaptor.capture(),requestBookingStateArgumentCaptor.capture());

        final Long actualUserId = longArgumentCaptor.getValue();
        final RequestBookingStateDto actualRequestBookingStateDto = requestBookingStateArgumentCaptor.getValue();

        assertEquals(actualUserId, userId);
        assertEquals(actualRequestBookingStateDto, RequestBookingStateDto.ALL);
    }

    @SneakyThrows
    @Test
    void addNewBooking() {
        final BookingRequestDto bookingRequestDto = new BookingRequestDto();
        final BookingResponseDto bookingResponseDto = new BookingResponseDto();
        final Long userId = 1L;
        final Long bookingId = 2L;

        bookingResponseDto.setId(bookingId);

        when(bookingService.addNew(any(BookingRequestDto.class), eq(userId))).thenReturn(bookingResponseDto);

        String response = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(bookingResponseDto), response);
    }

    @SneakyThrows
    @Test
    void approve() {
        final Long bookingId = 1L;
        final Long userId = 2L;
        final boolean approved = true;

        mockMvc.perform(patch("/bookings/{id}?approved={approved}", bookingId, approved)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(bookingService).approve(bookingId, userId, approved);
    }
}