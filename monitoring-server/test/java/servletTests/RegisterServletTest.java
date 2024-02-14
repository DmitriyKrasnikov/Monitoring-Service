package servletTests;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.user.UserService;
import servlet.RegisterServlet;
import utils.ServiceFactory;

import java.io.*;

public class RegisterServletTest {
    private RegisterServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserService userService;
    private Gson gson;

    @BeforeEach
    @DisplayName("Настройка тестового окружения перед каждым тестом")
    public void setUp() throws IOException {
        userService = Mockito.mock(UserService.class);
        gson = Mockito.mock(Gson.class);
        ServiceFactory.setUserService(userService);
        ServiceFactory.setGson(gson);
        servlet = new RegisterServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());
        Mockito.when(response.getWriter()).thenReturn(writer);
        String jsonUserDto = "{\"username\": \"uname\", \"email\": \"e@mail.com\", \"password\": \"testpassword\", \"salt\": null}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonUserDto));
        Mockito.when(request.getReader()).thenReturn(reader);
    }

    @Test
    @DisplayName("Тестирование метода doPost - успешная регистрация пользователя")
    public void testDoPostSuccessfulRegistration() throws Exception {
        UserDto userDto = new UserDto("testuser", "test@test.com", "testpassword");
        Mockito.when(gson.fromJson(Mockito.any(BufferedReader.class), Mockito.eq(UserDto.class))).thenReturn(userDto);
        Mockito.when(userService.register(Mockito.any(UserDto.class))).thenReturn("Registration was successful");

        servlet.doPost(request, response);

        Mockito.verify(response).setStatus(200);
    }

    @Test
    @DisplayName("Тестирование метода doPost - безуспешная регистрация пользователя")
    public void testDoPostFailedRegistration() throws Exception {
        UserDto userDto = new UserDto("testuser", "test@test.com", "testpassword");
        Mockito.when(gson.fromJson(Mockito.any(BufferedReader.class), Mockito.eq(UserDto.class))).thenReturn(userDto);
        Mockito.when(userService.register(Mockito.any(UserDto.class))).thenReturn("Registration failed");

        servlet.doPost(request, response);

        Mockito.verify(response).setStatus(400);
    }
}