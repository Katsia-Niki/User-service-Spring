package by.nikiforova.crud.controller;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Should get all users")
    public void testGetAllUsers() throws Exception {
        List<UserDto> users = Arrays.asList(
                new UserDto(2, 32, "Alice", "alice@example.com"),
                new UserDto(3, 23, "Bob", "bob@example.com")
        );

        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(2))
                .andExpect(jsonPath("$[0].name").value("Alice"))
                .andExpect(jsonPath("$[1].id").value(3))
                .andExpect(jsonPath("$[1].name").value("Bob"));
    }


    @Test
    @DisplayName("Should get user by id")
    public void testGetUserById() throws Exception {
        UserDto user = new UserDto(2, 32, "Alice", "alice@example.com");

        when(userService.getUserById(2)).thenReturn(user);

        mockMvc.perform(get("/api/users/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Alice"));
    }


    @Test
    @DisplayName("Should create user")
    public void testCreateUser() throws Exception {
        CreateUserDto createDto = new CreateUserDto("Charlie", "charlie@example.com", 25);
        UserDto responseDto = new UserDto(4, 25, "Charlie", "charlie@example.com");

        when(userService.createUser(any(CreateUserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.name").value("Charlie"));
    }


    @Test
    @DisplayName("Should update user")
    public void testUpdateUser() throws Exception {
        UpdateUserDto updateDto = new UpdateUserDto("UpdatedName", "updated_email@example.com", 28);
        UserDto responseDto = new UserDto(2, 28, "UpdatedName", "updated_email@example.com");

        when(userService.updateUser(eq(2), any(UpdateUserDto.class))).thenReturn(responseDto);


        mockMvc.perform(put("/api/users/2")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"));
    }


    @Test
    @DisplayName("Should delete user")
    public void testDeleteUser() throws Exception {
        doNothing().when(userService).deleteUser(2);

        mockMvc.perform(delete("/api/users/2"))
                .andExpect(status().isNoContent());
    }
}