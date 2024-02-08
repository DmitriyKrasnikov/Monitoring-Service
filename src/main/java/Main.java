import client.HttpMeterClient;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import model.readings.MeterReadings;
import model.readings.MeterType;
import model.user.dto.UserDto;
import server.HttpMeterServer;
import utils.DBInitializer;
import utils.ServiceFactory;

import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.time.Month;
import java.util.*;

/**
 * Главный класс приложения, содержащий метод main, который является точкой входа в приложение.
 */
public class Main {
    private static final HttpMeterClient client = new HttpMeterClient();
    private static final Scanner scanner = new Scanner(System.in);
    private static HttpMeterServer server;
    private static final Gson gson = ServiceFactory.getGson();

    public static void main(String[] args) throws Exception {
        DBInitializer.initialize();
        server = new HttpMeterServer();
        server.start();

        while (true) {
            System.out.println("""
                    Введите номер команды:
                    1 - Регистрация
                    2 - Вход
                    3 - Выход
                    4 - Получить текущие показания
                    5 - Отправить показания
                    6 - Показания за месяц
                    7 - История показаний
                    8 - Текущие показания пользователей
                    9 - Выход из приложения""");
            int command = scanner.nextInt();
            scanner.nextLine();
            switch (command) {
                case 1 -> register();
                case 2 -> login();
                case 3 -> logout();
                case 4 -> getCurrentReadings();
                case 5 -> postReadings();
                case 6 -> getReadingsByMonth();
                case 7 -> getReadingsHistory();
                case 8 -> getAllReadings();
                case 9 -> exitApplication();
                default -> System.out.println("Неизвестная команда");
            }
        }
    }

    private static void register() throws Exception {
        UserDto user = getUserData();
        HttpResponse<String> response = client.register(user);
        handleResponse(response, "Регистрация прошла успешно", "Ошибка при регистрации");
    }

    private static void login() throws Exception {
        UserDto user = getUserData();
        HttpResponse<String> response;
        try {
            response = client.login(user);
        }catch (IllegalStateException e){
            System.out.println(e.getMessage());
            return;
        }

        handleResponse(response, "Вход выполнен успешно", "Ошибка при входе");
    }

    private static void logout() throws Exception {
        HttpResponse<String> response = client.logout();
        handleResponse(response, "Вы вышли из системы", "Ошибка при выходе");
    }

    private static void getCurrentReadings() throws Exception {
        HttpResponse<String> response = client.getReadings();
        handleResponseAndPrintReadings(response, "Ошибка при получении показаний");
    }

    private static void postReadings() throws Exception {
        MeterReadings readings = getMeterReadings();
        HttpResponse<String> response = client.postReadings(readings);
        handleResponse(response, "Показания успешно отправлены", "Ошибка при отправке показаний");
    }

    private static void getReadingsByMonth() throws Exception {
        Month month = getMonth();
        HttpResponse<String> response = client.getReadingsByMonth(month);
        handleResponseAndPrintReadings(response, "Ошибка при получении показаний за месяц");
    }

    private static void getReadingsHistory() throws Exception {
        HttpResponse<String> response = client.getReadingsHistory();
        if (response != null && response.statusCode() == 200) {
            Type collectionType = new TypeToken<Collection<MeterReadings>>() {
            }.getType();
            Collection<MeterReadings> readingsHistory = gson.fromJson(response.body(), collectionType);
            if(readingsHistory == null || readingsHistory.isEmpty()){
                System.out.println("На данный момент показаний нет");
                return;
            }
            System.out.println("История показаний:");
            for (MeterReadings readings : readingsHistory) {
                printMeterReadings(gson.toJson(readings));
                System.out.println();
            }
        } else {
            System.out.println("Ошибка при получении истории показаний: " + (response != null ? response.body() : "Ответ от сервера отсутствует"));
        }
    }

    private static void getAllReadings() throws Exception {
        HttpResponse<String> response = client.getAllReadings();
        if (response != null && response.statusCode() == 200) {
            Type type = new TypeToken<Map<String, MeterReadings>>() {
            }.getType();
            Map<String, MeterReadings> allReadings = gson.fromJson(response.body(), type);
            if(allReadings == null || allReadings.isEmpty()){
                System.out.println("На данный момент показаний нет");
                return;
            }
            System.out.println("Все текущие показания пользователей:");
            for (Map.Entry<String, MeterReadings> entry : allReadings.entrySet()) {
                System.out.println("Пользователь: " + entry.getKey());
                printMeterReadings(gson.toJson(entry.getValue()));
                System.out.println();
            }
        } else {
            System.out.println("Ошибка при получении всех показаний: " + (response != null ? response.body() : "Ответ от сервера отсутствует"));
        }
    }

    private static UserDto getUserData() {
        System.out.println("Введите имя пользователя:");
        String username = scanner.nextLine();
        System.out.println("Введите email:");
        String email = scanner.nextLine();
        System.out.println("Введите пароль:");
        String password = scanner.nextLine();
        return new UserDto(username, email, password);
    }

    private static MeterReadings getMeterReadings() {
        Month month = getMonth();
        Map<MeterType, Integer> readings = getReadings();

        return new MeterReadings(readings, month);
    }

    private static Month getMonth() {
        System.out.println("Выберите месяц:");
        String[] months = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};
        for (int i = 0; i < months.length; i++) {
            System.out.println((i + 1) + " - " + months[i]);
        }
        int monthIndex = scanner.nextInt() - 1;
        while (monthIndex < 0 || monthIndex > 11){
            System.out.println("Введите корректный индекс месяца");
            monthIndex = scanner.nextInt() - 1;
        }
        scanner.nextLine(); // consume newline left-over
        return Month.values()[monthIndex];
    }

    private static Map<MeterType, Integer> getReadings() {
        Map<MeterType, Integer> readings = new HashMap<>();
        for (MeterType meterType : METER_TYPE_TRANSLATIONS.keySet()) {
            while (true) {
                try {
                    System.out.println("Введите показания для " + METER_TYPE_TRANSLATIONS.get(meterType) + ":");
                    int reading = scanner.nextInt();
                    scanner.nextLine(); // consume newline left-over
                    readings.put(meterType, reading);
                    break;
                } catch (InputMismatchException e) {
                    System.out.println("Ошибка: введено некорректное значение. Пожалуйста, введите целое число.");
                    scanner.nextLine(); // consume newline left-over
                }
            }
        }
        return readings;
    }

    private static void handleResponse(HttpResponse<String> response, String successMessage, String errorMessage) {
        if (response != null && response.statusCode() == 200) {
            System.out.println(successMessage + ": " + response.body());
        } else {
            System.out.println(errorMessage + ": " + (response != null ? response.body() : "Ответ от сервера отсутствует"));
        }
    }

    private static void exitApplication() {
        server.stop();
        System.out.println("Приложение завершило работу");
        System.exit(0);
    }

    private static final Map<Month, String> MONTH_TRANSLATIONS = Map.ofEntries(
            Map.entry(Month.JANUARY, "Январь"),
            Map.entry(Month.FEBRUARY, "Февраль"),
            Map.entry(Month.MARCH, "Март"),
            Map.entry(Month.APRIL, "Апрель"),
            Map.entry(Month.MAY, "Май"),
            Map.entry(Month.JUNE, "Июнь"),
            Map.entry(Month.JULY, "Июль"),
            Map.entry(Month.AUGUST, "Август"),
            Map.entry(Month.SEPTEMBER, "Сентябрь"),
            Map.entry(Month.OCTOBER, "Октябрь"),
            Map.entry(Month.NOVEMBER, "Ноябрь"),
            Map.entry(Month.DECEMBER, "Декабрь")
    );

    private static final Map<MeterType, String> METER_TYPE_TRANSLATIONS = Map.of(
            MeterType.HOT_WATER, "Горячая вода",
            MeterType.HEATING, "Отопление",
            MeterType.COLD_WATER, "Холодная вода"
    );

    private static void printMeterReadings(String json) {
        MeterReadings readings = gson.fromJson(json, MeterReadings.class);
        if(readings == null){
            System.out.println("На данный момент показаний нет");
            return;
        }
        System.out.println("Месяц: " + MONTH_TRANSLATIONS.get(readings.getMonth()));
        for (Map.Entry<MeterType, Integer> entry : readings.getReadings().entrySet()) {
            System.out.println(METER_TYPE_TRANSLATIONS.get(entry.getKey()) + ": " + entry.getValue());
        }
    }

    private static void handleResponseAndPrintReadings(HttpResponse<String> response, String errorMessage) {
        if (response != null && response.statusCode() == 200) {
            printMeterReadings(response.body());
        } else {
            System.out.println(errorMessage + ": " + (response != null ? response.body() : "Ответ от сервера отсутствует"));
        }
    }
}