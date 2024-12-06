package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Captor
    ArgumentCaptor<UserDto> userArgumentCaptor;

    @Captor
    ArgumentCaptor<Long> longArgumentCaptor;

    @SneakyThrows
    @Test
    void getById() {
        final Long userId = 1L;

        mockMvc.perform(get("/users/{id}", userId))
                .andExpect(status().isOk());
        verify(userService).getById(userId);
    }

    @SneakyThrows
    @Test
    void addNewUser() {
        final UserDto userDto = ObjectsFactory.newUserDto("email", "name");

        when(userService.addNew(any(UserDto.class))).thenReturn(userDto);

        String response = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(userDto), response);
    }

    @SneakyThrows
    @Test
    void updateUser() {
        final UserDto userDto = ObjectsFactory.newUserDto("email", "name");

        when(userService.update(any(UserDto.class))).thenReturn(userDto);

        String response = mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        verify(userService).update(userArgumentCaptor.capture());
        final UserDto updatedUser = userArgumentCaptor.getValue();

        assertEquals(objectMapper.writeValueAsString(userDto), response);
        assertEquals(updatedUser.getId(), 1L);
        assertEquals(updatedUser.getName(), userDto.getName());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userService).delete(longArgumentCaptor.capture());
        final Long deleteId = longArgumentCaptor.getValue();

        assertEquals(deleteId, 1L);
    }
}