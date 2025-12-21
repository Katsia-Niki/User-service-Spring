package by.nikiforova.crud.controller;

import by.nikiforova.crud.dto.CreateUserDto;
import by.nikiforova.crud.dto.UpdateUserDto;
import by.nikiforova.crud.dto.UserDto;
import by.nikiforova.crud.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger logger = LogManager.getLogger();

    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
            summary = "Создать пользователя",
            description = "Создает нового пользователя на основе переданных данных"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные пользователя")
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityModel<UserDto> createUser(@RequestBody @Valid CreateUserDto dto) {
        UserDto created = userService.createUser(dto);
        return toModel(created);
    }

    @Operation(
            summary = "Получить пользователя по ID",
            description = "Возвращает пользователя по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @GetMapping("/{id}")
    public EntityModel<UserDto> getUser(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable @NotNull @Min(1) @Max(1_000_000) Integer id) {
        UserDto user = userService.getUserById(id);
        return toModel(user);
    }

    @Operation(
            summary = "Получить список всех пользователей",
            description = "Возвращает список всех пользователей"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Список пользователей получен")
    })
    @GetMapping
    public CollectionModel<EntityModel<UserDto>> getAllUsers() {
        List<EntityModel<UserDto>> users = userService.getAllUsers()
                .stream()
                .map(this::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(
                users,
                linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel()
        );
    }

    @Operation(
            summary = "Обновить пользователя",
            description = "Обновляет данные пользователя по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь обновлен",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @PutMapping(path = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public EntityModel<UserDto> updateUser(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable @NotNull @Min(1) @Max(1_000_000) Integer id,
            @RequestBody @Valid UpdateUserDto dto) {
        UserDto updated = userService.updateUser(id, dto);
        return toModel(updated);
    }

    @Operation(
            summary = "Удалить пользователя",
            description = "Удаляет пользователя по его идентификатору"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Пользователь удален"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(
            @Parameter(description = "Идентификатор пользователя", example = "1")
            @PathVariable @NotNull @Min(1) Integer id) {
        userService.deleteUser(id);
    }

    /**
     * Преобразует UserDto в HATEOAS-модель с ссылками.
     */
    private EntityModel<UserDto> toModel(UserDto user) {
        return EntityModel.of(
                user,
                linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users")
        );
    }
}
