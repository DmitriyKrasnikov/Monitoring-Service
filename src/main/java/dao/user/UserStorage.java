package dao.user;

/**
 * Интерфейс UserStorage определяет контракт для хранения и обработки данных пользователя. Основной реализацией
 * является класс UserStorageImpl
 */
public interface UserStorage {

    void addNewUser(String username, String email, String password);

    boolean isRegister(String email);

    Integer getUserIdFromEmail(String email);

    Integer getUserIdFromName(String email);

    boolean isAdmin(String email);

    boolean validateUser(String email, String password);
}

