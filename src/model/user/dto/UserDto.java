package model.user.dto;

/**
 * Класс UserDto представляет собой модель данных для передачи информации о пользователе.
 * Используется при регистрации и входе.
 */
public class UserDto {
    private final String username;
    private final String password;

    public UserDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
