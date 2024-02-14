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

@Loggable
@WebServlet("/meter/readings/all")
public class AllReadingsServlet extends HttpServlet {
    private MeterService meterService;
    private Gson gson;

    public void init() {
        this.meterService = ServiceFactory.getMeterService();
        this.gson = ServiceFactory.getGson();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        boolean isAdmin = Token.getIsAdminFromToken(token);
        if (!isAdmin) {
            response.getWriter().write("Bad request. You don't have access rights");
            response.setStatus(401);
        } else {
            String readings = gson.toJson(meterService.getAllCurrentReadings());
            response.getWriter().write(readings);
            response.setStatus(200);
        }
    }
}