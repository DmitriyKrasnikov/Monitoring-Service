package utils;

import java.util.Base64;

/**
 * Класс для работы с токенами.
 */
public class Token {
    /**
     * Генерирует токен на основе данных пользователя.
     *
     * @param userId   идентификатор пользователя
     * @param email    электронная почта пользователя
     * @param username имя пользователя
     * @param isAdmin  является ли пользователь администратором
     * @return сгенерированный токен
     */
    public static String generateToken(Integer userId, String email, String username, boolean isAdmin) {
        String toCode = userId + ":" + email + ":" + username + ":" + isAdmin;
        return Base64.getEncoder().encodeToString(toCode.getBytes());
    }

    /**
     * Извлекает идентификатор пользователя из токена.
     *
     * @param encodedString закодированная строка токена
     * @return идентификатор пользователя
     */
    public static Integer getUserIdFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 0 ? Integer.parseInt(parts[0]) : null;
    }

    /**
     * Извлекает электронную почту пользователя из токена.
     *
     * @param encodedString закодированная строка токена
     * @return электронная почта пользователя
     */
    public static String getEmailFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 1 ? parts[1] : null;
    }

    /**
     * Извлекает имя пользователя из токена.
     *
     * @param encodedString закодированная строка токена
     * @return имя пользователя
     */
    public static String getUsernameFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    /**
     * Извлекает информацию о том, является ли пользователь администратором, из токена.
     *
     * @param encodedString закодированная строка токена
     * @return true, если пользователь является администратором, иначе false
     */
    public static Boolean getIsAdminFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 3 ? Boolean.parseBoolean(parts[3]) : null;
    }
}