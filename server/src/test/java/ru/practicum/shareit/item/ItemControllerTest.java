package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.ObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;

    @Captor
    ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    ArgumentCaptor<ItemDto> itemDtoArgumentCaptor;

    @SneakyThrows
    @Test
    void getById() {
        final Long itemId = 1L;

        mockMvc.perform(get("/items/{id}", itemId))
                .andExpect(status().isOk());
        verify(itemService).getByIdWithComments(itemId);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        final String text = "";

        mockMvc.perform(get("/items/search?text="+text))
                .andExpect(status().isOk());
        verify(itemService).searchItems(stringArgumentCaptor.capture());
        final String actualText = stringArgumentCaptor.getValue();

        assertEquals(actualText, text);
    }

    @SneakyThrows
    @Test
    void getByOwner() {
        final Long ownerId = 1L;

        mockMvc.perform(get("/items")
                .header("X-Sharer-User-Id", ownerId)
                .contentType("application/json"))
                .andExpect(status().isOk());
        verify(itemService).getByOwner(longArgumentCaptor.capture());
        final Long actualOwnerId = longArgumentCaptor.getValue();

        assertEquals(ownerId, actualOwnerId);
    }

    @SneakyThrows
    @Test
    void addNewItem() {
        final NewItemDto itemDto = ObjectsFactory.newNewItemDto("item", "item");
        final Long ownerId = 1L;

        when(itemService.addNewItem(any(NewItemDto.class), eq(ownerId))).thenReturn(itemDto);

        String response = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(itemDto), response);
    }

    @SneakyThrows
    @Test
    void updateItem() {
        final ItemDto itemDto = ObjectsFactory.newItemDto("item", "item");
        final Long itemId = 1L;
        final Long ownerId = 1L;

        itemDto.setId(itemId);
        when(itemService.updateItem(any(ItemDto.class), eq(ownerId))).thenReturn(itemDto);

        String response = mockMvc.perform(patch("/items/{id}", itemId)
                        .header("X-Sharer-User-Id", ownerId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(itemService).updateItem(itemDtoArgumentCaptor.capture(),longArgumentCaptor.capture());
        final ItemDto updatedItem = itemDtoArgumentCaptor.getValue();

        assertEquals(objectMapper.writeValueAsString(itemDto), response);
        assertEquals(updatedItem.getId(), itemId);
        assertEquals(updatedItem.getName(), itemDto.getName());
        assertEquals(updatedItem.getDescription(), itemDto.getDescription());
    }

    @SneakyThrows
    @Test
    void addNewComment() {
        final CommentDto commentDto = new CommentDto();
        final Long itemId = 1L;
        final Long userId = 1L;

        when(itemService.addNewComment(any(CommentDto.class), eq(itemId), eq(userId))).thenReturn(commentDto);

        String response = mockMvc.perform(post("/items/{id}/comment", itemId)
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(commentDto), response);
    }
}