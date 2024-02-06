package model.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import model.readings.MeterReadings;

import java.time.Month;
import java.util.Map;
import java.util.TreeMap;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class User {
    private Integer id;
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    private boolean isAdmin;
    private final Map<Month, MeterReadings> readingHistory = new TreeMap<>();

    public User(String username, String email, String password, boolean isAdmin) {
    }
}