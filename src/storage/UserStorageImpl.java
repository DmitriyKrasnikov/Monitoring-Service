package storage;

import model.readings.MeterReadings;
import model.user.User;

import java.time.Month;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Класс UserStorageImpl представляет собой реализацию интерфейса UserStorage.
 * Этот класс использует Map для хранения зарегистрированных пользователей.
 */
public class UserStorageImpl implements UserStorage {
    private final Map<String, User> registeredUsers = new HashMap<>();

    /**
     * Добавляет нового пользователя. Если это первый зарегистрированный пользователь, он становится администратором.
     * @see UserStorage#addNewUser(String, String)
     */
    @Override
    public void addNewUser(String username, String password) {
        if (!registeredUsers.isEmpty()) {
            registeredUsers.put(username, new User(username, password, false));
        } else {
            registeredUsers.put(username, new User(username, password, true));
        }
    }

    /**
     * Проверяет, зарегистрирован ли пользователь.
     * @see UserStorage#isRegister(String)
     */
    @Override
    public boolean isRegister(String name) {
        return registeredUsers.containsKey(name);
    }

    /**
     * Проверяет, является ли пользователь администратором.
     * @see UserStorage#isAdmin(String)
     */
    @Override
    public boolean isAdmin(String username) {
        return registeredUsers.get(username).isAdmin();
    }

    /**
     * Проверяет, действительны ли учетные данные пользователя.
     * @see UserStorage#validateUser(String, String)
     */
    @Override
    public boolean validateUser(String username, String password) {
        User user = registeredUsers.get(username);
        return user != null && user.getPassword().equals(password);
    }

    /**
     * Получает текущие показания счетчиков пользователя.
     * @see UserStorage#getCurrentReadings(String)
     */
    @Override
    public MeterReadings getCurrentReadings(String username) {
        User user = registeredUsers.get(username);
        return user != null ? user.getCurrentReadings() : null;
    }

    /**
     * Добавляет новые показания счетчиков для пользователя.
     * @see UserStorage#addNewReadings(String, MeterReadings)
     */
    @Override
    public void addNewReadings(String username, MeterReadings meterReadings) {
        User user = registeredUsers.get(username);
        if (user != null && meterReadings != null) {
            user.setCurrentReadings(meterReadings);
            user.getReadingHistory().put(meterReadings.getMonth(), meterReadings);
        }
    }

    /**
     * Получает показания счетчиков пользователя за определенный месяц.
     * @see UserStorage#getReadingsForMonth(String, Month)
     */
    @Override
    public MeterReadings getReadingsForMonth(String username, Month month) {
        User user = registeredUsers.get(username);
        return user != null ? user.getReadingHistory().get(month) : null;
    }

    /**
     * Получает историю показаний счетчиков пользователя.
     * @see UserStorage#getReadingsHistory(String)
     */
    @Override
    public Collection<MeterReadings> getReadingsHistory(String username) {
        User user = registeredUsers.get(username);
        return user != null ? user.getReadingHistory().values() : null;
    }

    /**
     * Получает текущие показания счетчиков всех пользователей.
     * @return Map, где ключ - имя пользователя, а значение - текущие показания счетчиков
     */
    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        Map<String, MeterReadings> readingsMap = new HashMap<>();
        for (User user : registeredUsers.values()) {
            String username = user.getUsername();
            MeterReadings currentReading = user.getCurrentReadings();
            readingsMap.put(username, currentReading);
        }
        return readingsMap;
    }
}
