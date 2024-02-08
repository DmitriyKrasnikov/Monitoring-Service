package utils;

import java.util.Base64;

public class Token {
    public static String generateToken(Integer userId, String email, String username, boolean isAdmin) {
        String toCode = userId + ":" + email + ":" + username + ":" + isAdmin;
        return Base64.getEncoder().encodeToString(toCode.getBytes());
    }

    public static Integer getUserIdFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 0 ? Integer.parseInt(parts[0]) : null;
    }

    public static String getEmailFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 1 ? parts[1] : null;
    }

    public static String getUsernameFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 2 ? parts[2] : null;
    }

    public static Boolean getIsAdminFromToken(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);
        String token = new String(decodedBytes);
        String[] parts = token.split(":");
        return parts.length > 3 ? Boolean.parseBoolean(parts[3]) : null;
    }
}