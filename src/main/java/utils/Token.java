package main.java.utils;

import java.util.Base64;

/**
 * Класс Token предоставляет методы для генерации и обработки токенов.
 */
public class Token {
    /**
     * Метод для генерации токена.
     * @param username Имя пользователя.
     * @param isAdmin Флаг, указывающий, является ли пользователь администратором.
     * @return Сгенерированный токен.
     */
    public static String generateToken(String username, boolean isAdmin){
        String toCode = username + ":" + isAdmin;
        return Base64.getEncoder().encodeToString(toCode.getBytes());
    }

    /**
     * Метод для получения имени пользователя из токена.
     * @param encodedString Закодированная строка токена.
     * @return Имя пользователя, извлеченное из токена.
     */
    public static String getUsernameFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 0 ? parts[0] : null;
    }

    /**
     * Метод для проверки, является ли пользователь администратором на основе токена.
     * @param encodedString Закодированная строка токена.
     * @return Булево значение, указывающее, является ли пользователь администратором.
     */
    public static Boolean getIsAdminFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 1 ? Boolean.parseBoolean(parts[1]) : null;
    }
}

