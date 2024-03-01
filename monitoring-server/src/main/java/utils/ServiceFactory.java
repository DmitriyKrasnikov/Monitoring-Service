package utils;

import annotations.Loggable;
import com.google.gson.Gson;
import dao.audit.AuditRepository;
import dao.audit.AuditRepositoryImpl;
import dao.readings.ReadingsRepository;
import dao.readings.ReadingsRepositoryImpl;
import dao.user.UserRepository;
import dao.user.UserRepositoryImpl;
import service.audit.AuditService;
import service.audit.AuditServiceImpl;
import service.reading.MeterService;
import service.reading.MeterServiceImpl;
import service.user.UserService;
import service.user.UserServiceImpl;

@Loggable
public class ServiceFactory {

    private static MeterService meterService;
    private static UserService userService;
    private static AuditService auditService;
    private static ReadingsRepository readingsRepository;
    private static UserRepository userRepository;
    private static AuditRepository auditRepository;
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

    public static UserRepository getUserStorage() {
        if (userRepository == null) {
            userRepository = new UserRepositoryImpl();
        }
        return userRepository;
    }

    public static ReadingsRepository getReadingsStorage() {
        if (readingsRepository == null) {
            readingsRepository = new ReadingsRepositoryImpl();
        }
        return readingsRepository;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public static AuditRepository getAuditStorage() {
        if (auditRepository == null) {
            auditRepository = new AuditRepositoryImpl();
        }
        return auditRepository;
    }

    public static AuditService getAuditService() {
        if (auditService == null) {
            auditService = new AuditServiceImpl();
        }
        return auditService;
    }
    public static void setMeterService(MeterService meterService) {
        ServiceFactory.meterService = meterService;
    }

    public static void setUserService(UserService userService) {
        ServiceFactory.userService = userService;
    }

    public static void setAuditService(AuditService auditService) {
        ServiceFactory.auditService = auditService;
    }

    public static void setReadingsRepository(ReadingsRepository readingsRepository) {
        ServiceFactory.readingsRepository = readingsRepository;
    }

    public static void setUserRepository(UserRepository userRepository) {
        ServiceFactory.userRepository = userRepository;
    }

    public static void setAuditRepository(AuditRepository auditRepository) {
        ServiceFactory.auditRepository = auditRepository;
    }

    public static void setGson(Gson gson) {
        ServiceFactory.gson = gson;
    }
}