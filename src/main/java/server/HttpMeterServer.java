package main.java.server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import main.java.model.readings.MeterReadings;
import main.java.model.user.dto.UserDto;
import main.java.service.MeterService;
import main.java.utils.LoggerConfig;
import main.java.utils.ServiceFactory;
import main.java.utils.Token;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Month;
import java.util.logging.Logger;

/**
 * Класс HttpMeterServer представляет собой HTTP-сервер для обработки запросов от клиентов.
 */
public class HttpMeterServer {
    public static final int PORT = 8080;
    private static final Logger logger = LoggerConfig.getLogger();

    private final HttpServer server;
    public final MeterService meterService;
    private final Gson gson;

    /**
     * Конструктор класса HttpMeterServer.
     * @throws IOException если сервер не может быть создан
     * Поля meterService и gson инициализируются с помощью утилитного класса ServiceFactory
     */
    public HttpMeterServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/meter", this::handle);
        this.meterService = ServiceFactory.getMeterService();
        this.gson = ServiceFactory.getGson();
    }

    /**
     * Запускает сервер.
     */
    public void start() {
        System.out.println("Сервер работает на " + PORT + " порте");
        server.start();
    }

    /**
     * Обрабатывает HTTP-запросы.
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     */
    public void handle(HttpExchange httpExchange) {
        try (httpExchange) {
            String uri = httpExchange.getRequestURI().getPath().replaceFirst("/meter/", "");
            UriEnum uriEnum = UriEnum.getUriEnum(uri);

            if (uriEnum == UriEnum.REGISTER || uriEnum == UriEnum.LOGIN) {
                handleLoginOrRegister(uriEnum, httpExchange);
            } else {
                handleOtherRequests(uriEnum, httpExchange);
            }
        } catch (IOException e) {
            System.out.println("Ошибка при обработке запроса: " + e.getMessage());
        }
    }

    /**
     * Обрабатывает запросы на регистрацию или вход.
     * @param uriEnum объект UriEnum, представляющий URI запроса
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleLoginOrRegister(UriEnum uriEnum, HttpExchange httpExchange) throws IOException {
        try {
            String userDtoJson = readText(httpExchange);
            UserDto userDto = gson.fromJson(userDtoJson, UserDto.class);
            processRequest(uriEnum, userDto, httpExchange);
        } catch (JsonSyntaxException e) {
            sendText(httpExchange, "Bad request. Please, check the entered data", 400);
        }
    }

    /**
     * Обрабатывает другие запросы.
     * @param uriEnum объект UriEnum, представляющий URI запроса
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleOtherRequests(UriEnum uriEnum, HttpExchange httpExchange) throws IOException {
        Headers headers = httpExchange.getRequestHeaders();
        checkAuthorization(headers, httpExchange);

        String authHeader = headers.getFirst("Authorization");
        String token = authHeader.replaceFirst("Bearer ", "");
        String username = Token.getUsernameFromToken(token);
        boolean isAdmin = Token.getIsAdminFromToken(token);

        logger.info("User " + username + " is trying to " + uriEnum);
        switch (uriEnum) {
            case LOGOUT -> sendText(httpExchange, meterService.logout(username), 200);
            case READINGS -> handleReadings(httpExchange, username);
            case READINGS_HISTORY -> sendText(httpExchange, gson.toJson(meterService.getReadingHistory(username)), 200);
            case READINGS_ALL -> handleAllReadings(httpExchange, isAdmin);
        }
    }

    /**
     * Обрабатывает запросы на регистрацию или вход.
     * @param uri объект UriEnum, представляющий URI запроса
     * @param userDto объект UserDto, представляющий данные пользователя
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void processRequest(UriEnum uri, UserDto userDto, HttpExchange httpExchange) throws IOException {
        logger.info("User " + userDto.getUsername() + " is trying to " + uri);
        switch (uri) {
            case REGISTER -> processRegistration(userDto, httpExchange);
            case LOGIN -> processLogin(userDto, httpExchange);
        }
    }

    /**
     * Обрабатывает запрос на регистрацию.
     * @param userDto объект UserDto, представляющий данные пользователя
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void processRegistration(UserDto userDto, HttpExchange httpExchange) throws IOException {
        String registerResult = meterService.register(userDto);
        int responseCode = registerResult.equals("Registration was successful") ? 200 : 400;
        sendText(httpExchange, registerResult, responseCode);
    }

     /**
     * Обрабатывает запрос на вход в систему.
     * @param userDto объект UserDto, представляющий данные пользователя
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void processLogin(UserDto userDto, HttpExchange httpExchange) throws IOException {
        String loginResult = meterService.login(userDto);
        int responseCode = loginResult.equals("You are logged in") ? 200 : 401;

        if (responseCode == 200) {
            loginResult += " " + Token.generateToken(userDto.getUsername(), meterService.isAdmin(userDto.getUsername()));
        }
        sendText(httpExchange, loginResult, responseCode);
    }

    /**
     * Проверяет авторизацию пользователя.
     * @param headers заголовки HTTP-запроса
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void checkAuthorization(Headers headers, HttpExchange httpExchange) throws IOException {
        if (!headers.containsKey("Authorization")) {
            sendText(httpExchange, "Bad request. Please, Log in", 401);
        }

        String authHeader = headers.getFirst("Authorization");
        String token = authHeader.replaceFirst("Bearer ", "");

        if (!meterService.isLogin(Token.getUsernameFromToken(token))) {
            sendText(httpExchange, "Bad request. Please, Log in", 401);
        }
    }

    /**
     * Обрабатывает запросы на чтение данных.
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @param username имя пользователя
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleReadings(HttpExchange httpExchange, String username) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();

        if (query != null) {
            String[] params = query.split("=");
            if (params.length > 1 && params[0].equals("month")) {
                String monthString = params[1];
                Month month = Month.valueOf(monthString.toUpperCase());
                sendText(httpExchange, gson.toJson(meterService.getReadingsForMonth(username, month)), 200);
            }
        }

        switch (httpExchange.getRequestMethod()) {
            case "GET" -> sendText(httpExchange, gson.toJson(meterService.getCurrentReadings(username)), 200);
            case "POST" -> {
                try {
                    MeterReadings meterReadings = gson.fromJson(readText(httpExchange), MeterReadings.class);
                    String registerResult = meterService.postCurrentReadings(username, meterReadings);
                    int responseCode = registerResult.equals("Readings added") ? 200 : 400;
                    sendText(httpExchange, registerResult, responseCode);
                } catch (JsonSyntaxException e) {
                    sendText(httpExchange, "Bad request. Please, check the entered data", 400);
                }
            }
        }
    }

    /**
     * Обрабатывает запросы на чтение всех данных.
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @param isAdmin флаг, указывающий, является ли пользователь администратором
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void handleAllReadings(HttpExchange httpExchange, boolean isAdmin) throws IOException {
        if (!isAdmin) {
            sendText(httpExchange, "Bad request. You don't have access rights", 401);
        }
        sendText(httpExchange, gson.toJson(meterService.getAllCurrentReadings()), 200);
    }

    /**
     * Читает текст из HTTP-запроса.
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @return текст из HTTP-запроса
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    /**
     * Отправляет текстовый ответ.
     * @param httpExchange объект HttpExchange, представляющий HTTP-запрос
     * @param responseString строка ответа
     * @param responseCode код ответа
     * @throws IOException если происходит ошибка ввода-вывода
     */
    private void sendText(HttpExchange httpExchange, String responseString, int responseCode) throws IOException {
        if (responseString.isBlank()) {
            httpExchange.sendResponseHeaders(responseCode, 0);
            logger.info("Sending an empty response with a code:" + responseCode);
        } else {
            byte[] resp = responseString.getBytes(StandardCharsets.UTF_8);
            httpExchange.sendResponseHeaders(responseCode, resp.length);
            httpExchange.getResponseBody().write(resp);
            logger.info("Sending response : " + responseString + " with a code: " + responseCode);
        }
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановили на " + PORT + " порту");
    }
}