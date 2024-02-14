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

@Loggable
@WebServlet("/meter/logout")
public class LogoutServlet extends HttpServlet {
    private UserService userService;

    public void init() {
        this.userService = ServiceFactory.getUserService();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = Token.getUsernameFromToken(request.getHeader("Authorization").replaceFirst("Bearer ", ""));
        String logoutResult = userService.logout(username);
        response.getWriter().write(logoutResult);
        response.setStatus(200);
    }
}
