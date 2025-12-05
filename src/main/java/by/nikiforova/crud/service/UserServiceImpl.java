package by.nikiforova.crud.service;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.exception.*;
import by.nikiforova.crud.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LogManager.getLogger();
    private final ModelMapper mapper = new ModelMapper();
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserServiceImpl constructor");
    }

    @Override
    @Transactional
    public UserDto createUser(CreateUserDto createUserDto) {
        logger.info("Идёт создание пользователя name={} email={}",
                createUserDto.getName(), createUserDto.getEmail());

        if (userRepository.existsByEmail(createUserDto.getEmail())) {
            throw new UserAlreadyExistsException("Пользователь с email " + createUserDto.getEmail() + " уже существует");
        }

        User user = userRepository.save(mapper.map(createUserDto, User.class));
        UserDto userDto = mapper.map(user, UserDto.class);

        logger.info("Пользователь создан name={} email={}", userDto.getName(), userDto.getEmail());

        return userDto;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(Integer id) {
        logger.info("Получение пользователя по id={}", id);
        return userRepository.findById(id).map(user -> mapper.map(user, UserDto.class))
                .orElseThrow(() -> new UserNotFoundException("User c id:" + id + " не найден"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.info("Получение списка всех пользователей");
        return userRepository.findAll().stream()
                .map(user -> mapper.map(user, UserDto.class)).toList();
    }

    @Override
    @Transactional
    public UserDto updateUser(Integer userDtoId, UpdateUserDto  updateUserDto) {
        logger.info("Обновление пользователя id={} name={}",  userDtoId, updateUserDto.getName());

        if (userRepository.existsByEmailAndIdNot(updateUserDto.getEmail(), userDtoId)) {
            throw new UserAlreadyExistsException("Пользователь с email " + updateUserDto.getEmail() + " уже существует");
        }
        User user = userRepository.findById(userDtoId)
                .orElseThrow(() -> new UserNotFoundException("User c id:" + userDtoId + " не найден."));

        user.setName(updateUserDto.getName());
        user.setEmail(updateUserDto.getEmail());
        user.setAge(updateUserDto.getAge());

        mapper.map(updateUserDto, user);
        User updatedUser = userRepository.save(user);
        logger.info("Пользователь обновлен id={}", user.getId());
        return mapper.map(updatedUser, UserDto.class);
    }

    @Override
    @Transactional
    public void deleteUser(Integer id) {
        logger.info("Удаление пользователя по id={}", id);
        if (userRepository.deleteUserById(id) == 0){
            throw new UserNotFoundException("User c id:" + id + " не найден.");
        }
        logger.info("Пользователь удален id={}", id);
    }
}
