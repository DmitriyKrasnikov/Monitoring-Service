package service.user;

import model.user.dto.UserDto;

public interface UserService {
    String register(UserDto userDto);

    String login(UserDto userDto);

    String logout(String username);

    boolean isLogin(String username);

    boolean isAdmin(String email);

    Integer getId(String email);
}
