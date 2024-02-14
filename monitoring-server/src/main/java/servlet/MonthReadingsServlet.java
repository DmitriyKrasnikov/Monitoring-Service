package servlet;

import annotations.Loggable;
import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.reading.MeterService;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;
import java.time.Month;

@Loggable
@WebServlet("/meter/readings/month")
public class MonthReadingsServlet extends HttpServlet {
    private MeterService meterService;
    private Gson gson;

    public void init() {
        this.meterService = ServiceFactory.getMeterService();
        this.gson = ServiceFactory.getGson();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        String monthString = request.getParameter("month");
        Month month = Month.valueOf(monthString.toUpperCase());
        String readings = gson.toJson(meterService.getReadingsForMonth(userId, month));
        response.getWriter().write(readings);
        response.setStatus(200);
    }
}
