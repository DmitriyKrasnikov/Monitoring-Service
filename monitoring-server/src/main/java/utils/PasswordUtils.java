package utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

/**
 * Утилитный класс для работы с паролями.
 */
public class PasswordUtils {

    /**
     * Генерирует соль для хеширования пароля.
     *
     * @return сгенерированная соль в виде шестнадцатеричной строки
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return bytesToHex(salt);
    }

    /**
     * Хеширует пароль с использованием соли.
     *
     * @param password пароль для хеширования
     * @param salt     соль для хеширования пароля
     * @return хешированный пароль в виде шестнадцатеричной строки
     * @throws RuntimeException если алгоритм SHA-256 не найден
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt.getBytes());
            byte[] hashedPassword = md.digest(password.getBytes());
            return bytesToHex(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Преобразует массив байтов в шестнадцатеричную строку.
     *
     * @param bytes массив байтов для преобразования
     * @return шестнадцатеричная строка
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }
}