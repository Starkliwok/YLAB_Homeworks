package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.Y_LAB.homework.dao.constants.SQLConstants.*;

/**
 * Класс ДАО слоя для взаимодействия с пользователями в базе данных
 * @author Денис Попов
 * @version 2.0
 */
@Repository
public class UserDAOImpl implements UserDAO {

    /** Поле для подключения к базе данных*/
    private final Connection connection;

    @Autowired
    public UserDAOImpl(DataSource dataSource) throws SQLException {
        connection = dataSource.getConnection();
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet userResultSet = statement.executeQuery(USER_GET_ALL);
            while (userResultSet.next()) {
                long id = userResultSet.getLong(1);
                String username = userResultSet.getString(2);
                String password = userResultSet.getString(3);
                if(isAdmin(id))
                    users.add(new Admin(id, username, password));
                else
                    users.add(new User(id, username, password));
            }
            statement.close();
            userResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return users;
    }

    @Override
    public User getUser(String username, String password) {
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(USER_GET_ID_WHERE_FIELDS);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                long id = userResultSet.getLong(1);
                if(isAdmin(id))
                    user = new Admin(id, username, password);
                else
                    user = new User(id, username, password);
            }
            statement.close();
            userResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return user;
    }

    @Override
    public Long getUserId(String username) {
        Long userId = null;
        try {
            PreparedStatement statement = connection.prepareStatement(USER_GET_ID_WHERE_NAME);
            statement.setString(1, username);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                userId = userResultSet.getLong(1);
            }
            statement.close();
            userResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return userId;
    }

    @Override
    public User getUser(long id) {
        User user = null;
        try {
            PreparedStatement statement = connection.prepareStatement(USER_GET_BY_ID);
            statement.setLong(1, id);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                String username = userResultSet.getString(1);
                String password = userResultSet.getString(2);
                if(isAdmin(id))
                    user = new Admin(id, username, password);
                else
                    user = new User(id, username, password);
            }
            statement.close();
            userResultSet.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return user;
    }

    @Override
    public boolean isUserExist(String username) {
        try {
            PreparedStatement statement = connection.prepareStatement(USER_GET_BY_NAME);
            statement.setString(1, username);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                statement.close();
                userResultSet.close();
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return false;
    }

    @Override
    public void saveUser(String username, String password) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(USER_FULL_INSERT);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(USER_FULL_UPDATE_WHERE_ID);
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    @Override
    public void deleteUser(long id) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(USER_DELETE_WHERE_ID);
            preparedStatement.setLong(1, id);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /**
     * Метод выявляет, является ли данный аккаунт администратором
     * @param id идентификационный номер аккаунта
     * @return true - если передан id администратора, false - если переданный id пользователя не является администратором
     */
    private boolean isAdmin(long id) throws SQLException {
        PreparedStatement statement = connection.prepareStatement(USER_FIND_IN_ADMIN_TABLE_WHERE_ID);
        statement.setLong(1, id);
        statement.execute();
        ResultSet resultSet = statement.getResultSet();
        boolean admin = resultSet.next();
        resultSet.close();
        statement.close();
        return admin;
    }
}