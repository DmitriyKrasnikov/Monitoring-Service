package dao.user;

import model.user.User;

import java.util.Optional;

/**
 * Интерфейс UserRepository определяет контракт для хранения и обработки данных пользователя. Основной реализацией
 * является класс UserRepositoryImpl
 */
public interface UserRepository {

    void addNewUser(String username, String email, String password, String salt);

    Optional<User> findByEmail(String email);

    Integer getUserIdFromName(String email);

    boolean validateUser(String email, String password);
}

