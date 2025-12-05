package by.nikiforova.crud.controller;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.service.UserService;
import jakarta.validation.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto createUser(@RequestBody @Valid CreateUserDto dto) {
        return userService.createUser(dto);
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable @NotNull @Min(2) @Max(50) Integer id) {
        return userService.getUserById(id);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public UserDto updateUser(@PathVariable @NotNull @Min(2) @Max(50) Integer id, @RequestBody @Valid UpdateUserDto dto) {
        return userService.updateUser(id, dto);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @NotNull @Min(2) Integer id) {
        userService.deleteUser(id);
    }
}
