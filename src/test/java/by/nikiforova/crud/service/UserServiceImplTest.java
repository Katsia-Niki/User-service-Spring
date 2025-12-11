package by.nikiforova.crud.service;


import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.exception.UserAlreadyExistsException;
import by.nikiforova.crud.exception.UserNotFoundException;
import by.nikiforova.crud.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    private static final Integer USER_ID = 1;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDto createUserDto;
    private UpdateUserDto updateUserDto;
    private UserDto userDto;
    private User user;

    @BeforeEach
    void setUp() {
        createUserDto = new CreateUserDto("Kate", "kate@gmail.com", 32);
        updateUserDto = new UpdateUserDto("Katerina", "kate_new@gmail.com", 32);
        userDto = new UserDto(1, 32, "Kate", "kate@gmail.com");
        user = new User("Kate", "kate@gmail.com", 32);
        user.setId(1);
    }

    @Test
    @DisplayName("Success")
    void createUserTest() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.createUser(createUserDto);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("UserAlreadyExistsException")
    void createUserExceptionTest() {
        when(userRepository.existsByEmail(createUserDto.getEmail())).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.createUser(createUserDto)
        );

        assertEquals("Пользователь с email " + createUserDto.getEmail() + " уже существует",
                exception.getMessage());
        verify(userRepository).existsByEmail(createUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Success")
    void getUserByIdTest() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(userDto, result);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("UserNotFoundException")
    void getUserByIdExceptionTest() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(USER_ID)
        );

        assertEquals("User c id:" + USER_ID + " не найден", exception.getMessage());
        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Success")
    void getAllUsersTest() {
        User user2 = new User("Polina", "polina.com", 20);
        user2.setId(2);
        UserDto userResponseDto2 = new UserDto(2, 20,"Polina", "polina.com");

        List<User> users = Arrays.asList(user, user2);
        List<UserDto> expectedResponse = Arrays.asList(userDto, userResponseDto2);

        when(userRepository.findAll()).thenReturn(users);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedResponse, result);
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Empty list")
    void getAllUsersEmptyListTest() {
        when(userRepository.findAll()).thenReturn(List.of());

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAll();
    }

    @Test
    @DisplayName("Success")
    void updateUserTest() {
        UserDto updatedUserDtoResponse = new UserDto(USER_ID, 32,"Katerina", "kate_new@gmail.com");

        when(userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID)).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.updateUser(USER_ID, updateUserDto);

        assertNotNull(result);
        assertEquals(updatedUserDtoResponse, result);
        verify(userRepository).existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID);
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("UserAlreadyExistsException")
    void updateUserUserAlreadyExistsExceptionTest() {
        when(userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID)).thenReturn(true);

        UserAlreadyExistsException exception = assertThrows(
                UserAlreadyExistsException.class,
                () -> userService.updateUser(USER_ID, updateUserDto)
        );

        assertEquals("Пользователь с email " + updateUserDto.getEmail() + " уже существует",
                exception.getMessage());
        verify(userRepository).existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID);
        verify(userRepository, never()).findById(USER_ID);
    }

    @Test
    @DisplayName("UserNotFoundException")
    void updateUserUserNotFoundExceptionTest() {
        when(userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID)).thenReturn(false);
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(USER_ID, updateUserDto)
        );

        assertEquals("User c id:" + USER_ID + " не найден.", exception.getMessage());
        verify(userRepository).existsByEmailAndIdNot(updateUserDto.getEmail(), USER_ID);
        verify(userRepository).findById(USER_ID);
    }

    @Test
    @DisplayName("Success")
    void deleteUserTest() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userRepository.deleteUserById(USER_ID)).thenReturn(1);

        assertDoesNotThrow(() -> userService.deleteUser(USER_ID));

        verify(userRepository).findById(USER_ID);
        verify(userRepository).deleteUserById(USER_ID);
    }

    @Test
    @DisplayName("UserNotFoundException")
    void deleteUserExceptionTest() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(USER_ID)
        );

        assertEquals("User c id:" + USER_ID + " не найден.", exception.getMessage());
        verify(userRepository).findById(USER_ID);
    }
}