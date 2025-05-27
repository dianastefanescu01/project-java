package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.repository.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserRepository implements IUserRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger(UserRepository.class);

    public UserRepository(Properties props){
        logger.info("Initializing UserRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<User> find(Integer id) {
        logger.traceEntry("find User with id {}", id);
        User user = null;
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Users WHERE user_id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
                user.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in find: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.ofNullable(user);
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("findAll Users");
        List<User> users = new ArrayList<>();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Users")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
                user.setId(resultSet.getInt("user_id"));
                users.add(user);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in findAll: " + e.getMessage());
        }
        logger.traceExit();
        return users;
    }

    @Override
    public Optional<User> save(User entity) {
        logger.traceEntry("save User {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO Users (username, email, password) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2, entity.getEmail());
            preparedStatement.setString(3, entity.getPassword());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Insert failed, no rows affected.");
                return Optional.empty();
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in save: " + e.getMessage());
            return Optional.empty();
        }
        logger.traceExit();
        return Optional.of(entity);
    }

    @Override
    public Optional<User> delete(Integer id) {
        logger.traceEntry("delete User with id {}", id);
        Optional<User> user = find(id);
        if (user.isPresent()) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Users WHERE user_id = ?")) {
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("User with id {} deleted", id);
                    return user;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error in delete: " + e.getMessage());
            }
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public Optional<User> update(User entity) {
        logger.traceEntry("update User {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE Users SET username = ?, email = ?, password = ? WHERE user_id = ?")) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setString(2,entity.getEmail());
            preparedStatement.setString(3, entity.getPassword());
            preparedStatement.setInt(4, entity.getId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("User with id {} updated", entity.getId());
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in update: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.empty();
    }

    @Override
    public User getUserByEmailAndPass(String email, String password){
        logger.traceEntry("getUserByEmailAndPass {}", email, password);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "SELECT * FROM Users WHERE email = ? AND password = ?")) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                User user = new User(
                        resultSet.getString("username"),
                        resultSet.getString("email"),
                        resultSet.getString("password")
                );
                user.setId(resultSet.getInt("user_id"));
                logger.info("User with email {} and password {} found", email, password);
                return user;
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in getUserByEmailAndPass: " + e.getMessage());
        }
        logger.traceExit("No user found with this email and password");
        return null;
    }
}
