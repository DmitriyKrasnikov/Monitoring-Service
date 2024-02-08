package utils;

import com.google.gson.Gson;
import dao.audit.AuditStorage;
import dao.audit.AuditStorageImpl;
import dao.readings.ReadingsStorage;
import dao.readings.ReadingsStorageImpl;
import dao.user.UserStorage;
import dao.user.UserStorageImpl;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;
import service.reading.MeterService;
import service.reading.MeterServiceImpl;
import service.user.UserService;
import service.user.UserServiceImpl;

public class ServiceFactory {

    private static MeterService meterService;
    private static UserService userService;
    private static AuditService auditService;
    private static ReadingsStorage readingsStorage;
    private static UserStorage userStorage;
    private static AuditStorage auditStorage;
    private static Gson gson;

    private ServiceFactory() {
    }

    public static UserService getUserService() {
        if (userService == null) {
            userService = new UserServiceImpl();
        }
        return userService;
    }

    public static MeterService getMeterService() {
        if (meterService == null) {
            meterService = new MeterServiceImpl();
        }
        return meterService;
    }

    public static UserStorage getUserStorage() {
        if (userStorage == null) {
            userStorage = new UserStorageImpl();
        }
        return userStorage;
    }

    public static ReadingsStorage getReadingsStorage() {
        if (readingsStorage == null) {
            readingsStorage = new ReadingsStorageImpl();
        }
        return readingsStorage;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static AuditStorage getAuditStorage() {
        if (auditStorage == null) {
            auditStorage = new AuditStorageImpl();
        }
        return auditStorage;
    }

    public static AuditService getAuditService() {
        if (auditService == null) {
            auditService = new AuditServiceImpl();
        }
        return auditService;
    }
}