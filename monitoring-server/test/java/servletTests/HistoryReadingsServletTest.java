package servletTests;

import com.google.gson.Gson;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.readings.MeterReadings;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import service.reading.MeterService;
import servlet.HistoryReadingsServlet;
import utils.ServiceFactory;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class HistoryReadingsServletTest {
    private HistoryReadingsServlet servlet;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private MeterService meterService;
    private Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        meterService = Mockito.mock(MeterService.class);
        gson = Mockito.mock(Gson.class);
        ServiceFactory.setMeterService(meterService);
        ServiceFactory.setGson(gson);
        servlet = new HistoryReadingsServlet();
        servlet.init();
        request = Mockito.mock(HttpServletRequest.class);
        response = Mockito.mock(HttpServletResponse.class);
        PrintWriter writer = new PrintWriter(new StringWriter());
        Mockito.when(response.getWriter()).thenReturn(writer);
    }

    @Test
    public void testDoGet() throws Exception {
        Mockito.when(request.getHeader("Authorization")).thenReturn("Bearer MToZQG1haWwuY29tOnVuYW1lOmZhbHNl");
        List<MeterReadings> readingsHistory = new ArrayList<>();
        Mockito.when(meterService.getReadingHistory(1)).thenReturn(readingsHistory);
        Mockito.when(gson.toJson(readingsHistory)).thenReturn("[]");

        servlet.doGet(request, response);

        Mockito.verify(response).setStatus(200);
    }
}