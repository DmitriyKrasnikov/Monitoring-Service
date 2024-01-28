package main.java.utils;

import com.google.gson.Gson;
import main.java.service.MeterService;
import main.java.service.MeterServiceImpl;
import main.java.storage.UserStorage;
import main.java.storage.UserStorageImpl;

/**
 * Класс ServiceFactory предоставляет статические методы для получения экземпляров сервисов.
 * Это реализация шаблона проектирования Singleton.
 */
public class ServiceFactory {

    private static MeterService meterService;
    private static UserStorage userStorage;
    private static Gson gson;

    /**
     * Приватный конструктор, чтобы предотвратить создание экземпляра класса.
     */
    private ServiceFactory() {
    }

    /**
     * Метод для получения экземпляра сервиса MeterService.
     * @return Экземпляр MeterService.
     */
    public static MeterService getMeterService() {
        if (meterService == null) {
            meterService = new MeterServiceImpl();
        }
        return meterService;
    }

    /**
     * Метод для получения экземпляра хранилища пользователей UserStorage.
     * @return Экземпляр UserStorage.
     */
    public static UserStorage getUserStorage() {
        if (userStorage == null) {
            userStorage = new UserStorageImpl();
        }
        return userStorage;
    }

    /**
     * Метод для получения экземпляра Gson.
     * @return Экземпляр Gson.
     */
    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }
}
