package com.khaliuk.dao;

import com.khaliuk.Factory;
import com.khaliuk.model.Category;
import com.khaliuk.model.Role;
import com.khaliuk.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserDaoImpl implements UserDao {

    private final Connection connection;

    public UserDaoImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public User addUser(User user) {
        String query = "INSERT INTO USERS VALUES(?, ?, ?, ?, ?, ?)";
        PreparedStatement statement;
        try {
            statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            statement.setNull(1, -5);
            statement.setString(2, user.getUsername());
            statement.setString(3, user.getPassword());
            statement.setString(4, user.getToken());
            statement.setString(5, user.getFirstName());
            statement.setString(6, user.getLastName());
            statement.executeUpdate();
            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                user.setId(keys.getLong(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public User getByToken(String token) {
        String query = "SELECT U.ID AS U_ID, " +
                "U.USERNAME, " +
                "U.PASSWORD, " +
                "U.TOKEN, " +
                "U.FIRST_NAME, " +
                "U.LAST_NAME, " +
                "R.ID AS R_ID, " +
                "R.ROLE_NAME " +
                "FROM USERS U " +
                "JOIN USERS_TO_ROLES UTR ON U.ID = UTR.FK_USER_ID " +
                "JOIN ROLES R ON UTR.FK_ROLE_ID = R.ID " +
                "WHERE U.TOKEN = ?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        User result = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, token);
            resultSet = statement.executeQuery();
            result = getUserWithRolesFromResultSet(resultSet);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    private User getUserWithRolesFromResultSet(ResultSet rs) throws SQLException {
        List<Role> roles = new ArrayList<>();
        User result = null;
        if (rs.next()) {
            result = getUserFromResultSet(rs);
            while (!rs.isAfterLast()) {
                roles.add(getRoleFromResultSet(rs));
                rs.next();
            }
            result.setRoles(roles);
        }
        return result;
    }

    private User getUserFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("u_id");
        String username = rs.getString("username");
        String password = rs.getString("password");
        String token = rs.getString("token");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        return new User(id, username, password, token, firstName, lastName);
    }

    private Role getRoleFromResultSet(ResultSet rs) throws SQLException {
        Long id = rs.getLong("r_id");
        String roleName = rs.getString("role_name");
        return new Role(id, Role.RoleName.valueOf(roleName));
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(Factory.getConnection());
        User user = userDao.getByToken("token4");
        System.out.println(user);
    }
}
