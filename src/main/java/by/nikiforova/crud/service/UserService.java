package by.nikiforova.crud.service;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(CreateUserDto createUserDto);
    UserDto getUserById(Integer id);
    List<UserDto> getAllUsers();
    UserDto updateUser(Integer userDtoId, UpdateUserDto  updateUserDto);
    void deleteUser(Integer id);
}
