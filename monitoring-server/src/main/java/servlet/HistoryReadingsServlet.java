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

/**
 * Сервлет, который обрабатывает историю показаний счетчиков.
 * Он аннотирован @Loggable и @WebServlet("/meter/readings/history").
 */
@Loggable
@WebServlet("/meter/readings/history")
public class HistoryReadingsServlet extends HttpServlet {
    /**
     * Сервис для взаимодействия со счетчиками.*/
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
     * Возвращает историю показаний счетчика для пользователя.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        String readings = gson.toJson(meterService.getReadingHistory(userId));
        response.getWriter().write(readings);
        response.setStatus(200);
    }
}