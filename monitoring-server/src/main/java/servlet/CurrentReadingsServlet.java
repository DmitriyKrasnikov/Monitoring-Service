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

/**
 * Сервлет, который обрабатывает текущие показания счетчиков.
 * Он аннотирован @Loggable и @WebServlet("/meter/readings").
 */
@Loggable
@WebServlet("/meter/readings")
public class CurrentReadingsServlet extends HttpServlet {
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
     * Возвращает текущие показания счетчика для пользователя.
     *
     * @param request  HttpServletRequest
     * @param response HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String token = request.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        String readings = gson.toJson(meterService.getCurrentReadings(userId));
        response.getWriter().write(readings);
        response.setStatus(200);
    }

    /**
     * Обрабатывает POST-запросы.
     * Регистрирует текущие показания счетчика для пользователя.
     *
     * @param req  HttpServletRequest
     * @param resp HttpServletResponse
     * @throws ServletException если произошла ошибка, специфичная для сервлета
     * @throws IOException      если произошла ошибка ввода/вывода
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String token = req.getHeader("Authorization").replaceFirst("Bearer ", "");
        int userId = Token.getUserIdFromToken(token);
        MeterReadings meterReadings = gson.fromJson(req.getReader(), MeterReadings.class);
        String registerResult = meterService.postCurrentReadings(userId, meterReadings);
        int responseCode = registerResult.equals("Показания добавлены") ? 200 : 400;
        resp.getWriter().write(registerResult);
        resp.setStatus(responseCode);
    }
}