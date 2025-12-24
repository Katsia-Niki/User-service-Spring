package by.nikiforova.crud.service;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.exception.*;
import by.nikiforova.crud.kafka.KafkaProducer;
import by.nikiforova.crud.kafka.UserEvent;
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
    public final KafkaProducer kafkaProducer;

    public UserServiceImpl(UserRepository userRepository, KafkaProducer kafkaProducer) {
        this.userRepository = userRepository;
        this.kafkaProducer = kafkaProducer;
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

        // Отправляем событие в Kafka, но не блокируем создание при ошибках Kafka
        try {
            kafkaProducer.sendMessage(new UserEvent("CREATE", user.getEmail()));
        } catch (Exception e) {
            logger.error("Ошибка при отправке события CREATE в Kafka для пользователя email={}: {}", 
                    user.getEmail(), e.getMessage());
            // Не пробрасываем исключение, чтобы создание пользователя завершилось успешно
        }
        
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

        String email = userRepository.findById(id)
                .map(User::getEmail)
                .orElseThrow(() -> new UserNotFoundException("User c id:" + id + " не найден."));

        if (userRepository.deleteUserById(id) == 0){
            throw new UserNotFoundException("User c id:" + id + " не найден.");
        }
        
        // Отправляем событие в Kafka, но не блокируем удаление при ошибках Kafka
        try {
            kafkaProducer.sendMessage(new UserEvent("DELETE", email));
        } catch (Exception e) {
            logger.error("Ошибка при отправке события DELETE в Kafka для пользователя id={}, email={}: {}", 
                    id, email, e.getMessage());
            // Не пробрасываем исключение, чтобы удаление пользователя завершилось успешно
        }
        
        logger.info("Пользователь удален id={}", id);
    }
}
