package client;

import com.google.gson.Gson;
import config.ClientConfig;
import model.readings.MeterReadings;
import model.user.dto.UserDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Month;

/**
 * Клиент для взаимодействия с HTTP-сервером мониторинга.
 */
public class HttpMeterClient {
    private final HttpClient client;
    private String token = "";
    private final Gson gson;
    private static final String HOST = ClientConfig.HOST;
    private static final String PORT = ClientConfig.PORT;
    private static final String SERVER_PATH = "http://" + HOST + ":" + PORT + "/monitoring-server/meter/";

    /**
     * Конструктор по умолчанию.
     */
    public HttpMeterClient() {
        this.client = HttpClient.newHttpClient();
        this.gson = new Gson();
    }

    /**
     * Регистрация пользователя.
     *
     * @param user Объект UserDto, содержащий данные пользователя.
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> register(UserDto user) throws Exception {
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "register"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Вход пользователя.
     *
     * @param user Объект UserDto, содержащий данные пользователя.
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> login(UserDto user) throws Exception {
        if (!token.isBlank()){
            throw new IllegalStateException("Выйдите из системы, прежде чем войти с другими данными");
        }
        String json = gson.toJson(user);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        HttpHeaders headers = response.headers();
        if (response.statusCode() == 200) {
            this.token = headers.firstValue("Authorization").orElse("");
        }
        return response;
    }

    /**
     * Выход пользователя.
     *
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> logout() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "logout"))
                .header("Authorization", token)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() == 200){
            token = "";
        }
        return response;
    }

    /**
     * Получение показаний счетчика.
     *
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> getReadings() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "readings"))
                .header("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Отправка показаний счетчика.
     *
     * @param readings Объект MeterReadings, содержащий показания счетчика.
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> postReadings(MeterReadings readings) throws Exception {
        String json = gson.toJson(readings);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "readings"))
                .header("Content-Type", "application/json")
                .header("Authorization", token)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Получение показаний счетчика за месяц.
     *
     * @param month Месяц, за который требуется получить показания.
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> getReadingsByMonth(Month month) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "readings/month?month=" + month))
                .header("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Получение истории показаний счетчика.
     *
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> getReadingsHistory() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "readings/history"))
                .header("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Получение всех показаний счетчика.
     *
     * @return HttpResponse<String> Ответ сервера.
     * @throws Exception В случае ошибки ввода/вывода или проблем с URI.
     */
    public HttpResponse<String> getAllReadings() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI(SERVER_PATH + "readings/all"))
                .header("Authorization", token)
                .GET()
                .build();
        return client.send(request, HttpResponse.BodyHandlers.ofString());
    }

    /**
     * Проверка, вошел ли пользователь в систему.
     *
     * @return boolean Возвращает true, если пользователь вошел в систему, иначе false.
     */
    public boolean isLoggedIn() {
        return hasToken();
    }

    /**
     * Проверка, есть ли у пользователя токен.
     *
     * @return boolean Возвращает true, если у пользователя есть токен, иначе false.
     */
    private boolean hasToken() {
        return !token.isBlank();
    }
}