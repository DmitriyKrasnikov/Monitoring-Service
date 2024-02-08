package dao.mapper;

import model.user.User;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper {

    public User map(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            String username = resultSet.getString("username");
            String email = resultSet.getString("email");
            String password = resultSet.getString("password");
            boolean isAdmin = resultSet.getBoolean("is_admin");

            return new User(username, email, password, isAdmin);
        }
        return null;
    }
}