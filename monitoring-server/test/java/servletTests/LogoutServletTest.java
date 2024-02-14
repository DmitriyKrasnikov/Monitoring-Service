package servletTests;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.user.UserService;
import servlet.LogoutServlet;
import utils.ServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

public class LogoutServletTest {
    private LogoutServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserService userService;

    @BeforeEach
    @DisplayName("Настройка тестового окружения перед каждым тестом")
    public void setUp() throws IOException {
        userService = Mockito.mock(UserService.class);
        ServiceFactory.setUserService(userService);
        servlet = new LogoutServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("Тестирование метода doGet - успешный выход пользователя")
    public void testDoGet() throws Exception {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MTplQG1haWwuY29tOnVuYW1lOmZhbHNl");
        Mockito.when(userService.logout("uname")).thenReturn("You are logged out");

        servlet.doGet(request, response);

        Mockito.verify(response).setStatus(200);
    }
}