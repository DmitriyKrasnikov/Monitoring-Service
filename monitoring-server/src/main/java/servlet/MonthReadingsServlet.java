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

/**
 * Сервлет, который обрабатывает показания счетчиков за месяц.
 * Он аннотирован @Loggable и @WebServlet("/meter/readings/month").
 */
@Loggable
@WebServlet("/meter/readings/month")
public class MonthReadingsServlet extends HttpServlet {
    /**
     * Сервис для взаимодействия со счетчиками.
     */
    private MeterService meterService;

    /**
     * Объект Gson для преобразования объектов Java в JSON.
     */
    private Gson gson;

    /**
     * Инициализирует сервлет.
     * Устанавливает объекты meterService и gson.
     */
    public void init() {
        this.meterService = ServiceFactory.getMeterService();
        this.gson = ServiceFactory.getGson();
    }

    /**
     * Обрабатывает GET-запросы.
     * Возвращает показания счетчика за указанный месяц для пользователя.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
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