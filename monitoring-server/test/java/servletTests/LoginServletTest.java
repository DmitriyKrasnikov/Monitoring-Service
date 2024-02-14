package servletTests;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.user.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.user.UserService;
import servlet.LoginServlet;
import utils.PasswordUtils;
import utils.ServiceFactory;

import java.io.*;

public class LoginServletTest {
    private LoginServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserService userService;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        userService = Mockito.mock(UserService.class);
        gson = Mockito.mock(Gson.class);
        ServiceFactory.setUserService(userService);
        ServiceFactory.setGson(gson);
        servlet = new LoginServlet();
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
    public void testDoPostSuccessfulLogin() throws Exception {
        UserDto userDto = new UserDto("uname", "e@mail.com", "testpassword");
        Mockito.when(gson.fromJson(Mockito.any(BufferedReader.class), Mockito.eq(UserDto.class))).thenReturn(userDto);
        Mockito.when(userService.login(userDto)).thenReturn("You are logged in");
        Mockito.when(userService.getId(userDto.getEmail())).thenReturn(1);
        Mockito.when(userService.isAdmin(userDto.getEmail())).thenReturn(false);
        Mockito.when(userService.getSalt(userDto)).thenReturn(PasswordUtils.generateSalt());
        Mockito.when(userService.login(Mockito.any(UserDto.class))).thenReturn("You are logged in");

        servlet.doPost(request, response);

        Mockito.verify(response).setStatus(200);
        Mockito.verify(response).setHeader("Authorization", "Bearer MTplQG1haWwuY29tOnVuYW1lOmZhbHNl");
    }
}