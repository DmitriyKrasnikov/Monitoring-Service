package dao.mapper;

import model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public User map(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("user_id");
        String username = resultSet.getString("username");
        String email = resultSet.getString("email");
        String password = resultSet.getString("password");
        String salt = resultSet.getString("salt");
        boolean isAdmin = resultSet.getBoolean("is_admin");

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setSalt(salt);
        user.setAdmin(isAdmin);

        return user;
    }
}
