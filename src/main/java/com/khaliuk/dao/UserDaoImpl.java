package com.khaliuk.dao;

import com.khaliuk.model.Role;
import com.khaliuk.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserDaoImpl extends AbstractDao<User, Long> implements UserDao {

    public UserDaoImpl(Connection connection) {
        super(connection);
    }

    @Override
    public User save(User user) {
        return super.save(user);
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

    @Override
    public Optional<User> getByUsername(String username) {
        String query = "SELECT U.ID AS U_ID, " +
                "U.USERNAME, " +
                "U.PASSWORD, " +
                "U.TOKEN, " +
                "U.FIRST_NAME, " +
                "U.LAST_NAME " +
                "FROM USERS U " +
                "WHERE U.USERNAME = ?";
        PreparedStatement statement;
        ResultSet resultSet = null;
        User result = null;
        try {
            statement = connection.prepareStatement(query);
            statement.setString(1, username);
            resultSet = statement.executeQuery();
            if (resultSet.next()) {
                result = getUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.ofNullable(result);
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
}
