package model.user;

import model.readings.MeterReadings;

import java.time.Month;
import java.util.Map;
import java.util.TreeMap;

/**
 * Класс User представляет собой модель данных для хранения информации о пользователе.
 * Этот класс содержит информацию об имени пользователя, пароле, статусе администратора, текущих показаниях счетчиков
 * и истории показаний.
 */
public class User {
    private final String username;
    private String password;
    private boolean isAdmin;
    private MeterReadings currentReadings;
    private final Map<Month, MeterReadings> readingHistory = new TreeMap<>();

    public User(String username, String password, boolean isAdmin) {
        this.username = username;
        this.password = password;
        this.isAdmin = isAdmin;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public MeterReadings getCurrentReadings() {
        return currentReadings;
    }

    public void setCurrentReadings(MeterReadings currentReadings) {
        this.currentReadings = currentReadings;
    }

    public Map<Month, MeterReadings> getReadingHistory() {
        return readingHistory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;

        if (isAdmin != user.isAdmin) return false;
        if (!username.equals(user.username)) return false;
        if (!password.equals(user.password)) return false;
        if (!currentReadings.equals(user.currentReadings)) return false;
        return readingHistory.equals(user.readingHistory);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + (isAdmin ? 1 : 0);
        result = 31 * result + currentReadings.hashCode();
        result = 31 * result + readingHistory.hashCode();
        return result;
    }
}
