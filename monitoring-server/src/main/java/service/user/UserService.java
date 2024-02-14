package service.user;

import annotations.Loggable;
import model.user.dto.UserDto;

@Loggable
public interface UserService {
    String register(UserDto userDto);

    String login(UserDto userDto);

    String getSalt(UserDto userDto);

    String logout(String username);

    boolean isLogin(String username);

    boolean isAdmin(String email);

    Integer getId(String email);
}
