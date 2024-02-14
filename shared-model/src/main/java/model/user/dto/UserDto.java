package model.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    @NonNull
    private String username;
    @NonNull
    private String email;
    @NonNull
    private String password;
    private String salt;

    public UserDto(String username, String email, String password){
        this.username = username;
        this.email = email;
        this.password = password;
    }
}