package servlet;

import annotations.Loggable;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.dto.UserDto;
import service.user.UserService;
import utils.PasswordUtils;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Сервлет, который обрабатывает вход в систему.
 * Он аннотирован @Loggable и @WebServlet("/meter/login").
 */
@Loggable
@WebServlet("/meter/login")
public class LoginServlet extends HttpServlet {
    /**
     * Сервис для взаимодействия с пользователями.
     */
    private UserService userService;

    /**
     * Объект Gson для преобразования объектов Java в JSON.
     */
    private Gson gson;

    /**
     * Инициализирует сервлет.
     * Устанавливает объекты userService и gson.
     */
    public void init() {
        this.userService = ServiceFactory.getUserService();
        this.gson = ServiceFactory.getGson();
    }

    /**
     * Обрабатывает POST-запросы.
     * Проверяет валидность данных пользователя и выполняет вход в систему.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserDto userDto = gson.fromJson(request.getReader(), UserDto.class);

        String validationError = validateUserDto(userDto);
        if (validationError != null) {
            response.getWriter().write(validationError);
            response.setStatus(400);
            return;
        }

        String salt = userService.getSalt(userDto);
        String hashedPassword = PasswordUtils.hashPassword(userDto.getPassword(), salt);

        UserDto userDtoWithHashedPassword = new UserDto(userDto.getUsername(), userDto.getEmail(), hashedPassword);

        String loginResult = userService.login(userDtoWithHashedPassword);
        int responseCode = loginResult.equals("Вы вошли в систему") ? 200 : 401;

        if (responseCode == 200) {
            String token = Token.generateToken(userService.getId(userDto.getEmail()), userDto.getEmail(),
                    userDto.getUsername(), userService.isAdmin(userDto.getEmail()));
            response.setHeader("Authorization", "Bearer " + token);
            loginResult = "Вы вошли в систему";
        }
        response.getWriter().write(loginResult);
        response.setStatus(responseCode);
    }

    /**
     * Проверяет валидность данных пользователя.
     *
     * @param userDto данные пользователя
     * @return сообщение об ошибке, если данные невалидны, иначе null
     */
    private String validateUserDto(UserDto userDto) {
        if (userDto.getUsername().isEmpty() || userDto.getUsername().length() < 3 || userDto.getUsername().length() > 50) {
            return "Имя пользователя должно быть от 3 до 50 символов";
        }
        if (userDto.getEmail().isEmpty() || !isValidEmail(userDto.getEmail())) {
            return "Email должен быть действительным";
        }
        if (userDto.getPassword().isEmpty() || userDto.getPassword().length() < 8) {
            return "Пароль должен быть длиной не менее 8 символов";
        }
        return null;
    }

    /**
     * Проверяет валидность email.
     *
     * @param email email для проверки
     * @return true, если email валидный, иначе false
     */
    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}