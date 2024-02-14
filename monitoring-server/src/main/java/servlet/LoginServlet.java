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

@Loggable
@WebServlet("/meter/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;
    private Gson gson;

    public void init() {
        this.userService = ServiceFactory.getUserService();
        this.gson = ServiceFactory.getGson();
    }

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
        int responseCode = loginResult.equals("You are logged in") ? 200 : 401;

        if (responseCode == 200) {
            String token = Token.generateToken(userService.getId(userDto.getEmail()), userDto.getEmail(),
                    userDto.getUsername(), userService.isAdmin(userDto.getEmail()));
            response.setHeader("Authorization", "Bearer " + token);
            loginResult = "You are logged in";
        }
        response.getWriter().write(loginResult);
        response.setStatus(responseCode);
    }

    private String validateUserDto(UserDto userDto) {
        if (userDto.getUsername().isEmpty() || userDto.getUsername().length() < 3 || userDto.getUsername().length() > 50) {
            return "Username must be between 3 and 50 characters";
        }
        if (userDto.getEmail().isEmpty() || !isValidEmail(userDto.getEmail())) {
            return "Email should be valid";
        }
        if (userDto.getPassword().isEmpty() || userDto.getPassword().length() < 8) {
            return "Password must be at least 8 characters long";
        }
        return null;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}