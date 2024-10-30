package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ComponentScan("ru/practicum/shareit")
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @Test
    void returnItemsListWhenGetSearch() throws Exception {
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        final ItemDto item2 = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        final List<ItemDto> items = Arrays.asList(item, item2);
        when(itemService.searchItems("search")).thenReturn(items);
        String response = this.mockMvc.perform(get("/items/search?text=search"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(items), response);
    }

    @Test
    void returnItemWhenPost() throws Exception {
        final ItemDto item = ObjectsFactory.newItemDto(ObjectsFactory.newStringValue(),
                ObjectsFactory.newStringValue(), 1L);
        item.setAvailable(true);
        when(itemService.saveItem(any(ItemDto.class))).thenReturn(item);
        String response = this.mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", "1")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(item)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(item), response);
    }
}