package main.java.service;

import main.java.model.readings.MeterReadings;
import main.java.model.user.dto.UserDto;

import java.time.Month;
import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс MeterService предоставляет методы для работы с пользователями и показаниями счетчиков.
 */
public interface MeterService {
    /**
     * Метод для регистрации нового пользователя.
     * @param userDto Объект UserDto, содержащий имя пользователя и пароль.
     * @return Строку с результатом регистрации.
     */
    String register(UserDto userDto);

    /**
     * Метод для входа пользователя в систему.
     * @param userDto Объект UserDto, содержащий имя пользователя и пароль.
     * @return Строку с результатом входа в систему.
     */
    String login(UserDto userDto);

    /**
     * Метод для выхода пользователя из системы.
     * @param username Имя пользователя.
     * @return Строку с результатом выхода из системы.
     */
    String logout(String username);

    /**
     * Метод для проверки, вошел ли пользователь в систему.
     * @param username Имя пользователя.
     * @return Булево значение, указывающее, вошел ли пользователь в систему.
     */
    boolean isLogin(String username);

    /**
     * Метод для проверки, является ли пользователь администратором.
     * @param username Имя пользователя.
     * @return Булево значение, указывающее, является ли пользователь администратором.
     */
    boolean isAdmin(String username);

    /**
     * Метод для получения текущих показаний счетчиков пользователя.
     * @param username Имя пользователя.
     * @return Объект MeterReadings, содержащий текущие показания счетчиков.
     */
    MeterReadings getCurrentReadings(String username);

    /**
     * Метод для отправки текущих показаний счетчиков пользователя.
     * @param username Имя пользователя.
     * @param readings Объект MeterReadings, содержащий текущие показания счетчиков.
     * @return Строку с результатом отправки показаний.
     */
    String postCurrentReadings(String username, MeterReadings readings);

    /**
     * Метод для получения показаний счетчиков пользователя за определенный месяц.
     * @param username Имя пользователя.
     * @param month Месяц, за который требуется получить показания.
     * @return Объект MeterReadings, содержащий показания счетчиков за указанный месяц.
     */
    MeterReadings getReadingsForMonth(String username, Month month);

    /**
     * Метод для получения истории показаний счетчиков пользователя.
     * @param username Имя пользователя.
     * @return Коллекцию объектов MeterReadings, содержащую историю показаний счетчиков.
     */
    Collection<MeterReadings> getReadingHistory(String username);

    /**
     * Метод для получения текущих показаний счетчиков всех пользователей.
     * @return Карту, где ключ - имя пользователя, а значение - объект MeterReadings, содержащий текущие показания счетчиков.
     */
    Map<String, MeterReadings> getAllCurrentReadings();

    /**
     * Метод для проверки корректности показаний счетчиков.
     * @param r Объект MeterReadings, содержащий показания счетчиков.
     * @return Булево значение, указывающее, являются ли показания корректными.
     */
    boolean validateReadings(MeterReadings r);
}
