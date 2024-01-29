package storage;

import model.readings.MeterReadings;

import java.time.Month;
import java.util.Collection;
import java.util.Map;

/**
 * Интерфейс UserStorage определяет контракт для хранения и обработки данных пользователя. Основной реализацией
 * является класс UserStorageImpl
 */
public interface UserStorage {
    /**
     * Добавляет нового пользователя.
     * @param username имя пользователя
     * @param password пароль пользователя
     */
    void addNewUser(String username, String password);

    /**
     * Проверяет, зарегистрирован ли пользователь.
     * @param name имя пользователя
     * @return true, если пользователь зарегистрирован, иначе false
     */
    boolean isRegister(String name);

    /**
     * Проверяет, является ли пользователь администратором.
     * @param username имя пользователя
     * @return true, если пользователь является администратором, иначе false
     */
    boolean isAdmin(String username);

    /**
     * Проверяет, действительны ли учетные данные пользователя.
     * @param username имя пользователя
     * @param password пароль пользователя
     * @return true, если учетные данные действительны, иначе false
     */
    boolean validateUser(String username, String password);

    /**
     * Получает текущие показания счетчиков пользователя.
     * @param username имя пользователя
     * @return текущие показания счетчиков пользователя
     */
    MeterReadings getCurrentReadings(String username);

    /**
     * Добавляет новые показания счетчиков для пользователя.
     * @param username имя пользователя
     * @param meterReadings новые показания счетчиков
     */
    void addNewReadings(String username, MeterReadings meterReadings);

    /**
     * Получает показания счетчиков пользователя за определенный месяц.
     * @param username имя пользователя
     * @param month месяц
     * @return показания счетчиков за месяц
     */
    MeterReadings getReadingsForMonth(String username, Month month);

    /**
     * Получает историю показаний счетчиков пользователя.
     * @param username имя пользователя
     * @return коллекцию показаний счетчиков
     */
    Collection<MeterReadings> getReadingsHistory(String username);

    /**
     * Получает текущие показания счетчиков всех пользователей.
     * @return Map, где ключ - имя пользователя, а значение - текущие показания счетчиков
     */
    Map<String, MeterReadings> getAllCurrentReadings();
}

