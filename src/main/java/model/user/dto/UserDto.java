package model.user.dto;

import lombok.NonNull;

public record UserDto(@NonNull String username, @NonNull String email, @NonNull String password) {
}