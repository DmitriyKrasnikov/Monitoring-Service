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
 * Сервлет, который обрабатывает все показания счетчиков.
 * Он аннотирован @Loggable и @WebServlet("/meter/readings/all").
 */
@Loggable
@WebServlet("/meter/readings/all")
public class AllReadingsServlet extends HttpServlet {
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
     * Если пользователь является администратором, он возвращает все текущие показания счетчиков.
     * Если пользователь не является администратором, он возвращает ошибку 401.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        boolean isAdmin = Token.getIsAdminFromToken(token);
        if (!isAdmin) {
            response.getWriter().write("Плохой запрос. У вас нет прав доступа");
            response.setStatus(401);
        } else {
            String readings = gson.toJson(meterService.getAllCurrentReadings());
            response.getWriter().write(readings);
            response.setStatus(200);
        }
    }
}