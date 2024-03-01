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
import servlet.CurrentReadingsServlet;
import utils.ServiceFactory;

import java.io.*;
import java.time.Month;
import java.util.HashMap;

public class CurrentReadingsServletTest {
    private CurrentReadingsServlet servlet;
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
        servlet = new CurrentReadingsServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());
        Mockito.when(response.getWriter()).thenReturn(writer);
        String jsonReadings = "{\"readings\": {\"HOT_WATER\": 120, \"HEATING\": 220, \"COLD_WATER\": 80}, \"month\": \"Январь\"}";
        BufferedReader reader = new BufferedReader(new StringReader(jsonReadings));
        Mockito.when(request.getReader()).thenReturn(reader);
    }

    @Test
    @DisplayName("Тестирование метода doGet - получение текущих показаний")
    public void testDoGet() throws Exception {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MToZQG1haWwuY29tOnVuYW1lOmZhbHNl");
        MeterReadings readings = new MeterReadings(new HashMap<>(), Month.JANUARY);
        Mockito.when(meterService.getCurrentReadings(1)).thenReturn(readings);
        Mockito.when(gson.toJson(readings)).thenReturn("{}");

        servlet.doGet(request, response);

        Mockito.verify(response).setStatus(200);
    }

    @Test
    @DisplayName("Тестирование метода doPost - добавление текущих показаний")
    public void testDoPost() throws Exception {
        // Настройка макетов
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MToZQG1haWwuY29tOnVuYW1lOmZhbHNl");
        MeterReadings readings = new MeterReadings(new HashMap<>(), Month.JANUARY);
        Mockito.when(gson.fromJson(Mockito.any(BufferedReader.class), Mockito.eq(MeterReadings.class))).thenReturn(readings);
        Mockito.when(meterService.postCurrentReadings(Mockito.anyInt(), Mockito.any(MeterReadings.class))).thenReturn("Readings added");

        // Вызов тестируемого метода
        servlet.doPost(request, response);

        // Проверка результата
        Mockito.verify(response).setStatus(200);
    }
}