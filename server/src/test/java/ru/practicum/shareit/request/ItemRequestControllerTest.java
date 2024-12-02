package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;

    @SneakyThrows
    @Test
    void addNewItemRequest() {
        final ItemRequestDto itemRequestDto = new ItemRequestDto();
        final Long userId = 2L;

        when(itemRequestService.addNewItemRequest(any(ItemRequestDto.class), eq(userId))).thenReturn(itemRequestDto);

        String response = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemRequestDto), response);
    }

    @SneakyThrows
    @Test
    void getByAuthor() {
        final Long userId = 1L;

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk());
        verify(itemRequestService).getByAuthor(longArgumentCaptor.capture());
        final Long actualUserId = longArgumentCaptor.getValue();

        assertEquals(actualUserId, userId);
    }

    @SneakyThrows
    @Test
    void getAll() {
        mockMvc.perform(get("/requests/all"))
                .andExpect(status().isOk());
        verify(itemRequestService).getAll();
    }

    @SneakyThrows
    @Test
    void getById() {
        final Long itemRequestId = 1L;
        mockMvc.perform(get("/requests/{id}", itemRequestId))
                .andExpect(status().isOk());

        verify(itemRequestService).getById(itemRequestId);
    }
}