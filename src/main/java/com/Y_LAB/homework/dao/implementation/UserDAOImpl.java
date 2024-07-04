package com.Y_LAB.homework.dao.implementation;

import com.Y_LAB.homework.dao.UserDAO;
import com.Y_LAB.homework.model.roles.Admin;
import com.Y_LAB.homework.model.roles.User;
import com.Y_LAB.homework.util.db.ConnectionToDatabase;
import lombok.AllArgsConstructor;

import java.sql.*;
import java.util.*;

/**
 * Класс ДАО слоя для взаимодействия с пользователями в базе данных
 * @author Денис Попов
 * @version 2.0
 */
@AllArgsConstructor
public class UserDAOImpl implements UserDAO {

    /** Поле для подключения к базе данных*/
    private final Connection connection;

    public UserDAOImpl() {
        connection = ConnectionToDatabase.getConnection();
    }

    /** {@inheritDoc}*/
    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try {
            Statement statement = connection.createStatement();
            ResultSet userResultSet = statement.executeQuery("SELECT * FROM coworking.user");
            while (userResultSet.next()) {
                long id = userResultSet.getLong(1);
                String username = userResultSet.getString(2);
                String password = userResultSet.getString(3);
                if(isUserHasRoot(id))
                    users.add(new Admin(id, username, password));
                else
                    users.add(new User(id, username, password));
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return users;
    }

    /** {@inheritDoc}*/
    @Override
    public User getUser(String username, String password) {
        User user = null;
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT id FROM coworking.user WHERE name = ? AND password = ?");
            statement.setString(1, username);
            statement.setString(2, password);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                long id = userResultSet.getLong(1);
                if(isUserHasRoot(id))
                    user = new Admin(id, username, password);
                else
                    user = new User(id, username, password);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return user;
    }

    /** {@inheritDoc}*/
    @Override
    public User getUser(long id) {
        User user = null;
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT name, password FROM coworking.user WHERE id = ?");
            statement.setLong(1, id);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                String username = userResultSet.getString(1);
                String password = userResultSet.getString(2);
                if(isUserHasRoot(id))
                    user = new Admin(id, username, password);
                else
                    user = new User(id, username, password);
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return user;
    }

    /** {@inheritDoc}*/
    @Override
    public boolean isUserExist(String username) {
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM coworking.user WHERE name = ?");
            statement.setString(1, username);
            statement.execute();
            ResultSet userResultSet = statement.getResultSet();
            if(userResultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return false;
    }

    /** {@inheritDoc}*/
    @Override
    public void saveUser(String username, String password) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("INSERT INTO coworking.user (name, password) VALUES (?, ?)");

            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void updateUser(User user) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("UPDATE coworking.user SET name = ?, password = ? WHERE id = ?");
            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setLong(3, user.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /** {@inheritDoc}*/
    @Override
    public void deleteUser(long id) {
        try {
            PreparedStatement preparedStatement =
                    connection.prepareStatement("DELETE FROM coworking.user WHERE id = ?");
            preparedStatement.setLong(1, id);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
    }

    /**
     * Метод выявляет, является ли данный аккаунт администратором
     * @param id идентификационный номер аккаунта
     * @return true - если передан id администратора, false - если переданный id пользователя не является администратором
     */
    private boolean isUserHasRoot(long id) {
        try {
            PreparedStatement statement =
                    connection.prepareStatement("SELECT * FROM coworking.admin WHERE user_id = ?");
            statement.setLong(1, id);
            statement.execute();
            return statement.getResultSet().next();
        } catch (SQLException e) {
            System.out.println("Произошла ошибка " + e.getMessage());
        }
        return false;
    }
}
