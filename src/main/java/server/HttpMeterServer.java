package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import model.readings.MeterReadings;
import model.user.dto.UserDto;
import org.slf4j.Logger;
import service.reading.MeterService;
import service.user.UserService;
import utils.LoggerConfig;
import utils.ServiceFactory;
import utils.Token;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Month;

public class HttpMeterServer {
    public static final int PORT = 8080;
    private static final Logger logger = LoggerConfig.getLogger();

    private final HttpServer server;
    public final MeterService meterService;
    public final UserService userService;

    private final Gson gson;

    public HttpMeterServer() throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/meter", this::handle);
        this.meterService = ServiceFactory.getMeterService();
        this.userService = ServiceFactory.getUserService();
        this.gson = ServiceFactory.getGson();
    }

    public void start() {
        System.out.println("Сервер работает на " + PORT + " порте");
        server.start();
    }

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

    private void handleLoginOrRegister(UriEnum uriEnum, HttpExchange httpExchange) throws IOException {
        try {
            String userDtoJson = readText(httpExchange);
            UserDto userDto = gson.fromJson(userDtoJson, UserDto.class);
            processRequest(uriEnum, userDto, httpExchange);
        } catch (JsonSyntaxException e) {
            sendText(httpExchange, "Bad request. Please, check the entered data", 400);
        }
    }

    private void handleOtherRequests(UriEnum uriEnum, HttpExchange httpExchange) throws IOException {
        Headers headers = httpExchange.getRequestHeaders();
        if (!checkAuthorization(headers, httpExchange)) {
            return;
        }

        String authHeader = headers.getFirst("Authorization");
        String token = authHeader.replaceFirst("Bearer ", "");
        Integer userId = Token.getUserIdFromToken(token);
        String username = Token.getUsernameFromToken(token);
        boolean isAdmin = Token.getIsAdminFromToken(token);

        logger.info("User " + username + " is trying to " + uriEnum);
        switch (uriEnum) {
            case LOGOUT -> sendText(httpExchange, userService.logout(username), 200);
            case READINGS -> handleReadings(httpExchange, userId);
            case READINGS_HISTORY -> sendText(httpExchange, gson.toJson(meterService.getReadingHistory(userId)), 200);
            case READINGS_ALL -> handleAllReadings(httpExchange, isAdmin);
        }
    }

    private void processRequest(UriEnum uri, UserDto userDto, HttpExchange httpExchange) throws IOException {
        logger.info("User " + userDto.username() + " is trying to " + uri);
        switch (uri) {
            case REGISTER -> processRegistration(userDto, httpExchange);
            case LOGIN -> processLogin(userDto, httpExchange);
        }
    }

    private void processRegistration(UserDto userDto, HttpExchange httpExchange) throws IOException {
        String registerResult = userService.register(userDto);
        int responseCode = registerResult.equals("Registration was successful") ? 200 : 400;
        sendText(httpExchange, registerResult, responseCode);
    }

    private void processLogin(UserDto userDto, HttpExchange httpExchange) throws IOException {
        String loginResult = userService.login(userDto);
        int responseCode = loginResult.equals("You are logged in") ? 200 : 401;

        if (responseCode == 200) {
            String token = Token.generateToken(userService.getId(userDto.email()), userDto.email(),
                    userDto.username(), userService.isAdmin(userDto.email()));
            httpExchange.getResponseHeaders().set("Authorization", "Bearer " + token);
            loginResult = "You are logged in";
        }
        sendText(httpExchange, loginResult, responseCode);
    }

    private boolean checkAuthorization(Headers headers, HttpExchange httpExchange) throws IOException {
        if (!headers.containsKey("Authorization")) {
            sendText(httpExchange, "Bad request. Please, Log in", 401);
            return false;
        }

        String authHeader = headers.getFirst("Authorization");
        String token = authHeader.replaceFirst("Bearer ", "");

        if (!userService.isLogin(Token.getUsernameFromToken(token))) {
            sendText(httpExchange, "Bad request. Please, Log in", 401);
            return false;
        }
        return true;
    }

    private void handleReadings(HttpExchange httpExchange, Integer userId) throws IOException {
        String query = httpExchange.getRequestURI().getQuery();

        if (query != null) {
            String[] params = query.split("=");
            if (params.length > 1 && params[0].equals("month")) {
                String monthString = params[1];
                Month month = Month.valueOf(monthString.toUpperCase());
                sendText(httpExchange, gson.toJson(meterService.getReadingsForMonth(userId, month)), 200);
            }
        }

        switch (httpExchange.getRequestMethod()) {
            case "GET" -> sendText(httpExchange, gson.toJson(meterService.getCurrentReadings(userId)), 200);
            case "POST" -> {
                try {
                    MeterReadings meterReadings = gson.fromJson(readText(httpExchange), MeterReadings.class);
                    String registerResult = meterService.postCurrentReadings(userId, meterReadings);
                    int responseCode = registerResult.equals("Readings added") ? 200 : 400;
                    sendText(httpExchange, registerResult, responseCode);
                } catch (JsonSyntaxException e) {
                    sendText(httpExchange, "Bad request. Please, check the entered data", 400);
                }
            }
        }
    }

    private void handleAllReadings(HttpExchange httpExchange, boolean isAdmin) throws IOException {
        if (!isAdmin) {
            sendText(httpExchange, "Bad request. You don't have access rights", 401);
        }
        sendText(httpExchange, gson.toJson(meterService.getAllCurrentReadings()), 200);
    }

    private String readText(HttpExchange httpExchange) throws IOException {
        return new String(httpExchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

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