package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ObjectsFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    void returnUsersListWhenGet() throws Exception {
        final UserDto user = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name");
        final UserDto user2 = ObjectsFactory.newUserDto(ObjectsFactory.newStringValue(), "name");
        final List<UserDto> users = Arrays.asList(user, user2);
        when(userService.getById(Mockito.anyList())).thenReturn(users);
        String response = this.mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<String> f = List.of("Шекспир","Байрон");
        assertEquals(objectMapper.writeValueAsString(users), response);
    }

    @Test
    void returnUserWhenPost() throws Exception {
        final UserDto user = ObjectsFactory.newUserDto("aaa@gg.com", "name");
        when(userService.saveUser(any(UserDto.class))).thenReturn(user);
        String response = this.mockMvc.perform(post("/users")
                .contentType("application/json")
                .content(this.objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(user), response);
    }

    @Test
    void returnUserWhenUpdate() throws Exception {
        final UserDto user = ObjectsFactory.newUserDto("aaa@gg.com", "name");
        when(userService.updateUser(any(UserDto.class))).thenReturn(user);
        String response = this.mockMvc.perform(patch("/users/1")
                        .contentType("application/json")
                        .content(this.objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertEquals(objectMapper.writeValueAsString(user), response);
    }
}