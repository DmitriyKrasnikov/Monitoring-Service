package servletTests;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.readings.MeterReadings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.reading.MeterService;
import servlet.AllReadingsServlet;
import utils.ServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class AllReadingsServletTest {
    private AllReadingsServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MeterService meterService;
    private Gson gson;

    @BeforeEach
    @DisplayName("Настройка тестового окружения перед каждым тестом")
    public void setUp() throws IOException {
        meterService = Mockito.mock(MeterService.class);
        gson = Mockito.mock(Gson.class);
        ServiceFactory.setMeterService(meterService);
        ServiceFactory.setGson(gson);
        servlet = new AllReadingsServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    @DisplayName("Тестирование метода doGet для администратора")
    public void testDoGetAdmin() throws Exception {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MToZQG1haWwuY29tOnVuYW1lOnRydWU=");
        Map<String, MeterReadings> readings = new HashMap<>();
        Mockito.when(meterService.getAllCurrentReadings()).thenReturn(readings);
        Mockito.when(gson.toJson(readings)).thenReturn("{}");

        servlet.doGet(request, response);

        Mockito.verify(response).setStatus(200);
    }

    @Test
    @DisplayName("Тестирование метода doGet для не администратора")
    public void testDoGetNonAdmin() throws Exception {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MToZQG1haWwuY29tOnVuYW1lOmZhbHNl");

        servlet.doGet(request, response);

        Mockito.verify(response).setStatus(401);
    }
}