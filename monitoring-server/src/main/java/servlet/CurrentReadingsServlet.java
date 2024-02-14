package servlet;

import annotations.Loggable;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.readings.MeterReadings;
import service.reading.MeterService;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;

@Loggable
@WebServlet("/meter/readings")
public class CurrentReadingsServlet extends HttpServlet {
    private MeterService meterService;
    private Gson gson;

    public void init() {
        this.meterService = ServiceFactory.getMeterService();
        this.gson = ServiceFactory.getGson();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        String readings = gson.toJson(meterService.getCurrentReadings(userId));
        response.getWriter().write(readings);
        response.setStatus(200);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        MeterReadings meterReadings = gson.fromJson(req.getReader(), MeterReadings.class);
        String registerResult = meterService.postCurrentReadings(userId, meterReadings);
        int responseCode = registerResult.equals("Readings added") ? 200 : 400;
        resp.getWriter().write(registerResult);
        resp.setStatus(responseCode);
    }
}