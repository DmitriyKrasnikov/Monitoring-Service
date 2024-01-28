package main.java.server;

/**
 * Enum UriEnum представляет собой набор возможных URI для обработки HTTP-запросов.
 * Это перечисление содержит URI для регистрации, входа в систему, выхода из системы, чтения данных и истории чтения.
 */
public enum UriEnum {
    REGISTER("register"),
    LOGIN("login"),
    LOGOUT("logout"),
    READINGS("readings"),
    READINGS_HISTORY("readings/history"),
    READINGS_ALL("readings/all");

    private final String uri;

    UriEnum(String uri) {
        this.uri = uri;
    }

    public String getUri() {
        return uri;
    }

    public static UriEnum getUriEnum(String uri) {
        for (UriEnum uriEnum : values()) {
            if (uriEnum.getUri().equals(uri)) {
                return uriEnum;
            }
        }
        throw new IllegalArgumentException("Invalid URI: " + uri);
    }
}

