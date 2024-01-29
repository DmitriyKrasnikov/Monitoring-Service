package service;

import model.readings.MeterReadings;
import model.user.dto.UserDto;
import storage.UserStorage;
import utils.ServiceFactory;

import java.time.Month;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Класс MeterServiceImpl реализует интерфейс MeterService.
 * Этот класс предоставляет методы для регистрации, входа в систему, выхода из системы пользователей, а также для работы с показаниями счетчиков.
 */
public class MeterServiceImpl implements MeterService {
    private final Set<String> onlineUsers = new HashSet<>();
    private final UserStorage userStorage = ServiceFactory.getUserStorage();

    /**
     * Метод для регистрации нового пользователя.
     * @see MeterService#register(UserDto)
     */
    @Override
    public String register(UserDto userDto) {
        if (userDto.getUsername() == null || userDto.getPassword() == null ||
                userDto.getUsername().isBlank() || userDto.getPassword().isBlank()) {
            return "Username or password cannot be null or empty";
        }

        if (userStorage.isRegister(userDto.getUsername())) {
            return "A user with that name already exists";
        }

        userStorage.addNewUser(userDto.getUsername(), userDto.getPassword());
        return "Registration was successful";
    }

    /**
     * Метод для входа пользователя в систему.
     * @see MeterService#login(UserDto)
     */
    @Override
    public String login(UserDto userDto) {
        if (!userStorage.validateUser(userDto.getUsername(), userDto.getPassword())) {
            return "Invalid username or password";
        }

        if (isLogin(userDto.getUsername())) {
            return "You are already logged in";
        }

        onlineUsers.add(userDto.getUsername());
        return "You are logged in";
    }

    /**
     * Метод для выхода пользователя из системы.
     * @see MeterService#logout(String)
     */
    @Override
    public String logout(String username) {
        onlineUsers.remove(username);
        return "You are logged out";
    }

    /**
     * Метод для проверки, вошел ли пользователь в систему.
     * @see MeterService#isLogin(String)
     */
    @Override
    public boolean isLogin(String username) {
        return onlineUsers.contains(username);
    }

    /**
     * Метод для проверки, является ли пользователь администратором.
     * @see MeterService#isAdmin(String)
     */
    @Override
    public boolean isAdmin(String username) {
        return userStorage.isAdmin(username);
    }

    /**
     * Метод для получения текущих показаний счетчиков пользователя.
     * @see MeterService#getCurrentReadings(String)
     */
    @Override
    public MeterReadings getCurrentReadings(String username) {
        return userStorage.getCurrentReadings(username);
    }

    /**
     * Метод для отправки текущих показаний счетчиков пользователя.
     * @see MeterService#postCurrentReadings(String, MeterReadings)
     */
    @Override
    public String postCurrentReadings(String username, MeterReadings readings) {
        if(getReadingHistory(username).contains(readings)){
            return "The readings for this month has already been submitted";
        }
        if (!validateReadings(readings)) {
            return "Incorrect readings";
        }
        userStorage.addNewReadings(username, readings);

        return "Readings added";
    }

    /**
     * Метод для получения показаний счетчиков пользователя за определенный месяц.
     * @see MeterService#getReadingsForMonth(String, Month)
     */
    @Override
    public MeterReadings getReadingsForMonth(String username, Month month) {
        return userStorage.getReadingsForMonth(username, month);
    }

    /**
     * Метод для получения истории показаний счетчиков пользователя.
     * @see MeterService#getReadingHistory(String)
     */
    @Override
    public Collection<MeterReadings> getReadingHistory(String username) {
        return userStorage.getReadingsHistory(username);
    }

    /**
     * Метод для получения текущих показаний счетчиков всех пользователей.
     * @see MeterService#getAllCurrentReadings()
     */
    @Override
    public Map<String, MeterReadings> getAllCurrentReadings() {
        return userStorage.getAllCurrentReadings();
    }

    /**
     * Метод для проверки корректности показаний счетчиков.
     * @see MeterService#validateReadings(MeterReadings)
     */
    @Override
    public boolean validateReadings(MeterReadings r) {
        if (r == null) {
            return false;
        }
        if (r.getColdWater() == null || r.getHotWater() == null || r.getHeating() == null || r.getMonth() == null) {
            return false;
        }
        return Stream.of(r.getColdWater(), r.getHotWater(), r.getHeating())
                .allMatch(val -> val > 0);
    }
}

