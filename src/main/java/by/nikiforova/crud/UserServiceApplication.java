package by.nikiforova.crud;


import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.entity.User;
import by.nikiforova.crud.repository.UserRepository;
import by.nikiforova.crud.service.UserService;
import by.nikiforova.crud.service.UserServiceImpl;
import jakarta.validation.ConstraintViolation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.function.Function;

@SpringBootApplication
public class UserServiceApplication {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        try {
            logger.info("Запуск user-service");
            SpringApplication.run(UserServiceApplication.class, args);
        } catch (Exception e) {
            logger.error("Критическая ошибка при запуске приложения", e);
        } finally {
            logger.info("Завершение работы user-service");
        }
    }
}
