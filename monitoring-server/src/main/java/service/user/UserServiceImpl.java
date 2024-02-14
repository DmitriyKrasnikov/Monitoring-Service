package service.user;

import annotations.Loggable;
import dao.user.UserRepository;
import model.audit.ActionType;
import model.user.User;
import model.user.dto.UserDto;
import service.audit.AuditService;
import utils.ServiceFactory;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Реализация сервиса пользователей.
 * Этот класс реализует интерфейс UserService и предоставляет методы для регистрации, входа, получения соли, выхода, проверки входа, проверки администратора и получения идентификатора пользователя.
 *
 * @Loggable Аннотация, указывающая, что вызовы методов этого класса должны быть залогированы.
 */
@Loggable
public class UserServiceImpl implements UserService {
    private final Set<String> onlineUsers = new HashSet<>();
    private final UserRepository userRepository = ServiceFactory.getUserStorage();
    private final AuditService auditService = ServiceFactory.getAuditService();

    /**
     * Регистрирует нового пользователя.
     *
     * @param userDto Объект UserDto, содержащий данные нового пользователя.
     * @return String Сообщение о результате операции.
     */
    @Override
    public String register(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isPresent()) {
            return "A user with that name already exists";
        }

        userRepository.addNewUser(userDto.getUsername(), userDto.getEmail(), userDto.getPassword(), userDto.getSalt());
        optionalUser = userRepository.findByEmail(userDto.getEmail());
        optionalUser.ifPresent(user -> auditService.recordAction(user.getId(), ActionType.REGISTER, "User registered"));
        return "Registration was successful";
    }

    /**
     * Вход пользователя.
     *
     * @param userDto Объект UserDto, содержащий данные пользователя.
     * @return String Сообщение о результате операции.
     */
    @Override
    public String login(UserDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        if (optionalUser.isEmpty() || !optionalUser.get().getPassword().equals(userDto.getPassword())) {
            return "Invalid username or password";
        }

        if (isLogin(optionalUser.get().getUsername())) {
            return "You are already logged in";
        }

        onlineUsers.add(optionalUser.get().getUsername());
        auditService.recordAction(optionalUser.get().getId(), ActionType.LOGIN, "User logged in");
        return "You are logged in";
    }

    /**
     * Получает соль пользователя.
     *
     * @param userDto Объект UserDto, содержащий данные пользователя.
     * @return String Соль пользователя.
     */
    @Override
    public String getSalt(UserDto userDto){
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());
        return optionalUser.map(User::getSalt).orElse(null);
    }

    /**
     * Выход пользователя.
     *
     * @param username Имя пользователя.
     * @return String Сообщение о результате операции.
     */
    @Override
    public String logout(String username) {
        onlineUsers.remove(username);
        auditService.recordAction(userRepository.getUserIdFromName(username),
                ActionType.LOGOUT, "User logged out");
        return "You are logged out";
    }

    /**
     * Проверяет, вошел ли пользователь.
     *
     * @param username Имя пользователя.
     * @return boolean Возвращает true, если пользователь вошел, иначе false.
     */
    @Override
    public boolean isLogin(String username) {
        return onlineUsers.contains(username);
    }

    /**
     * Проверяет, является ли пользователь администратором.
     *
     * @param email Электронная почта пользователя.
     * @return boolean Возвращает true, если пользователь является администратором, иначе false.
     */
    @Override
    public boolean isAdmin(String email) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.map(User::isAdmin).orElse(false);
    }

    /**
     * Получает идентификатор пользователя.
     *
     * @param email Электронная почта пользователя.
     * @return Integer Идентификатор пользователя.
     */
    @Override
    public Integer getId(String email) {
        return userRepository.findByEmail(email).map(User::getId).orElse(null);
    }
}