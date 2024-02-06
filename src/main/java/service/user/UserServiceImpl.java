package service.user;

import dao.user.UserStorage;
import model.audit.ActionType;
import model.user.dto.UserDto;
import service.audit.AuditService;
import utils.ServiceFactory;

import java.util.HashSet;
import java.util.Set;

public class UserServiceImpl implements UserService {
    private final Set<String> onlineUsers = new HashSet<>();
    private final UserStorage userStorage = ServiceFactory.getUserStorage();
    private final AuditService auditService = ServiceFactory.getAuditService();

    @Override
    public String register(UserDto userDto) {
        if (userStorage.isRegister(userDto.username())) {
            return "A user with that name already exists";
        }

        userStorage.addNewUser(userDto.username(), userDto.email(), userDto.password());
        auditService.recordAction(userStorage.getUserIdFromEmail(userDto.email()),
                ActionType.REGISTER, "User registered");
        return "Registration was successful";
    }

    @Override
    public String login(UserDto userDto) {
        if (!userStorage.validateUser(userDto.email(), userDto.password())) {
            return "Invalid username or password";
        }

        if (isLogin(userDto.username())) {
            return "You are already logged in";
        }

        onlineUsers.add(userDto.username());
        auditService.recordAction(userStorage.getUserIdFromEmail(userDto.email()),
                ActionType.LOGIN, "User logged in");
        return "You are logged in";
    }

    @Override
    public String logout(String username) {
        onlineUsers.remove(username);
        auditService.recordAction(userStorage.getUserIdFromName(username),
                ActionType.LOGIN, "User logged in");
        return "You are logged out";
    }

    @Override
    public boolean isLogin(String username) {
        return onlineUsers.contains(username);
    }

    @Override
    public boolean isAdmin(String email) {
        return userStorage.isAdmin(email);
    }

    @Override
    public Integer getId(String email) {
        return userStorage.getUserIdFromEmail(email);
    }
}