package by.nikiforova.crud.dto;

import jakarta.validation.constraints.*;

public class CreateUserDto {

    @NotNull(message = "Имя не может быть пустым")
    @Size(min = 2, max = 50, message = "Имя не должно иметь от 2 до 50 символов")
    private String name;

    @NotNull(message = "Email не может быть пустым")
    @Email(message = "Некорректный email адрес")
    private String email;

    @NotNull(message = "Возраст не может быть пустым")
    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 150, message = "Возраст не может быть больше 120")
    private Integer age;


    public CreateUserDto() {
    }

    public CreateUserDto(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }
}
