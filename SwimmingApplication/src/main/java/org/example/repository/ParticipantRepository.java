package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.User;
import org.example.utils.JdbcUtils;

import java.sql.*;
import java.util.*;

public class ParticipantRepository implements IParticipantRepository{

    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private UserRepository userRepository;

    public ParticipantRepository(Properties props){
        logger.info("Initializing ParticipantRepository with properties: {}", props);
        dbUtils = new JdbcUtils(props);
        this.userRepository = new UserRepository(props);
    }

    @Override
    public ArrayList<Participant> findByName(String name) {
        logger.traceEntry("FindParticipantsByName {}", name);
        ArrayList<Participant> participants = new ArrayList<>();
        Connection conn = dbUtils.getConnection();
        try(PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Participants WHERE name LIKE ?")){
            preparedStatement.setString(1,"%"+name+"%");
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                Participant participant = new Participant(
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("user_id")
                );
                participants.add(participant);
            }
        }catch (SQLException e){
            logger.error(e);
            System.err.println("Error in findByName: " + e.getMessage());
        }
        logger.traceExit();
        return participants;
    }

    @Override
    public Optional<Participant> find(Integer id) {
        logger.traceEntry("Find Participant with id {}", id);
        Participant participant = null;
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Participants WHERE participant_id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                participant = new Participant(
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("user_id")
                );
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in find: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.ofNullable(participant);
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry("findAll Participants");
        List<Participant> participants = new ArrayList<>();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Participants")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Participant participant = new Participant(
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getInt("user_id")
                );
                participants.add(participant);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in findAll: " + e.getMessage());
        }
        logger.traceExit();
        return participants;
    }

    @Override
    public Optional<Participant> save(Participant entity) {
        logger.traceEntry("save Participant {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO Participants (name, age, user_id) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setInt(2, entity.getAge());
            preparedStatement.setInt(3,entity.getUserId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Insert failed, no rows affected.");
                return Optional.empty();
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                System.out.println("Generated ID: " + generatedId);
                entity.setId(generatedId);
            } else {
                System.out.println("No ID generated!");
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
    public Optional<Participant> delete(Integer id) {
        logger.traceEntry("delete Participant with id {}", id);
        Optional<Participant> participant = find(id);
        if (participant.isPresent()) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Participants WHERE participant_id = ?")) {
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Participant with id {} deleted", id);
                    return participant;
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
    public Optional<Participant> update(Participant entity) {
        logger.traceEntry("update Participant {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE Participants SET name = ?, age = ?, user_id = ? WHERE participant_id = ?")) {
            preparedStatement.setString(1, entity.getName());
            preparedStatement.setInt(2, entity.getAge());
            preparedStatement.setInt(3,entity.getUserId());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Participant with id {} updated", entity.getID());
                return Optional.of(entity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in update: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.empty();
    }
}
