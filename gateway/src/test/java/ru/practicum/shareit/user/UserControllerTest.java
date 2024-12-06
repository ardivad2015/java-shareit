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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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
    private UserClient userClient;

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
        verify(userClient).getUser(userId);
    }

    @SneakyThrows
    @Test
    void addNewUser_whenDtoIsValid_thenOk() {
        final UserDto userDto = new UserDto();

        userDto.setEmail("aa@ff.com");
        userDto.setName("name");

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());
        verify(userClient).addUser(userArgumentCaptor.capture());
        final UserDto savedUser = userArgumentCaptor.getValue();

        assertEquals(savedUser.getEmail(), userDto.getEmail());
        assertEquals(savedUser.getEmail(), userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void addNewUser_whenDtoIsNotValid_thenBadRequestReturned() {
        final UserDto userDto = new UserDto();

        mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).addUser(any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void updateUser_whenDtoValid_thenOk() {
        final UserDto userDto = new UserDto();

        userDto.setEmail("aa@ff.com");
        userDto.setName("name");

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk());

        verify(userClient).updateUser(longArgumentCaptor.capture(), userArgumentCaptor.capture());
        final UserDto updatedUser = userArgumentCaptor.getValue();

        assertEquals(updatedUser.getEmail(), userDto.getEmail());
        assertEquals(updatedUser.getEmail(), userDto.getEmail());
    }

    @SneakyThrows
    @Test
    void updateUser_whenDtoIsNotValid_thenReturnedBadRequest() {
        final UserDto userDto = new UserDto();

        mockMvc.perform(patch("/users/{id}", 1L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isBadRequest());

        verify(userClient, never()).updateUser(any(Long.class), any(UserDto.class));
    }

    @SneakyThrows
    @Test
    void deleteUser() {
        mockMvc.perform(delete("/users/{id}", 1L))
                .andExpect(status().isOk());

        verify(userClient).deleteUser(longArgumentCaptor.capture());
        final Long deleteId = longArgumentCaptor.getValue();

        assertEquals(deleteId, 1L);
    }
}