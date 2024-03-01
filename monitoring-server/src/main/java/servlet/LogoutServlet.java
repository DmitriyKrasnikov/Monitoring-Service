package servlet;

import annotations.Loggable;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.user.UserService;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;

/**
 * Сервлет, который обрабатывает выход из системы.
 * Он аннотирован @Loggable и @WebServlet("/meter/logout").
 */
@Loggable
@WebServlet("/meter/logout")
public class LogoutServlet extends HttpServlet {
    /**
     * Сервис для взаимодействия с пользователями.
     */
    private UserService userService;

    /**
     * Инициализирует сервлет.
     * Устанавливает объект userService.
     */
    public void init() {
        this.userService = ServiceFactory.getUserService();
    }

    /**
     * Обрабатывает GET-запросы.
     * Выполняет выход из системы для пользователя.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = Token.getUsernameFromToken(request.getHeader("Authorization").replaceFirst("Bearer ", ""));
        String logoutResult = userService.logout(username);
        response.getWriter().write(logoutResult);
        response.setStatus(200);
    }
}