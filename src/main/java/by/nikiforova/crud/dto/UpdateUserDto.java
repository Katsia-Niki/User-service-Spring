package by.nikiforova.crud.dto;

import jakarta.validation.constraints.*;


public class UpdateUserDto {

    @Size(min = 2, max = 50, message = "Имя не должно иметь от 2 до 50 символов")
    private String name;

    @Email(message = "Некорректный email адрес")
    private String email;

    @Min(value = 0, message = "Возраст не может быть отрицательным")
    @Max(value = 120, message = "Возраст не может быть больше 120")
    private Integer age;


    public UpdateUserDto() {
    }

    public UpdateUserDto(String name, String email, Integer age) {
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UpdateUserDto{");
        sb.append("name='").append(name).append('\'');
        sb.append(", email='").append(email).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}