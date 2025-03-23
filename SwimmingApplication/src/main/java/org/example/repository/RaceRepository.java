package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Race;
import org.example.utils.JdbcUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RaceRepository implements IRaceRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();

    public RaceRepository(Properties props){
        logger.info("Initializing RaceRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
    }

    @Override
    public Optional<Race> find(Integer id) {
        logger.traceEntry("find Race with id {}", id);
        Race raceEntity = null;
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Races WHERE race_id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                raceEntity = new Race(
                        resultSet.getInt("distance"),
                        resultSet.getString("style"),
                        resultSet.getInt("nrOfParticipants")
                );
                raceEntity.setId(id);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in find: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.ofNullable(raceEntity);
    }

    @Override
    public Iterable<Race> findAll() {
        logger.traceEntry("findAll Races");
        List<Race> races = new ArrayList<>();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM Races")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Race raceEntity = new Race(
                        resultSet.getInt("distance"),
                        resultSet.getString("style"),
                        resultSet.getInt("nrOfParticipants")
                );
                raceEntity.setId(resultSet.getInt("race_id"));
                races.add(raceEntity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in findAll: " + e.getMessage());
        }
        logger.traceExit();
        return races;
    }

    @Override
    public Optional<Race> save(Race entity) {
        logger.traceEntry("save Race {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO Races (distance, style, nrOfParticipants) VALUES (?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, entity.getDistance());
            preparedStatement.setString(2, entity.getStyle());
            preparedStatement.setInt(3, entity.getNrOfParticipants());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Insert failed, no rows affected.");
                return Optional.empty();
            }
            ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                entity.setId(generatedKeys.getInt(1)); // Set the generated ID for the entity
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
    public Optional<Race> update(Race entity) {
        logger.traceEntry("update Race {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE Races SET distance = ?, style = ?, nrOfParticipants = ? WHERE race_id = ?")) {
            preparedStatement.setInt(1, entity.getDistance());
            preparedStatement.setString(2, entity.getStyle());
            preparedStatement.setInt(3, entity.getNrOfParticipants());
            preparedStatement.setInt(4,entity.getID());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Race with id {} updated", entity.getID());
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
    public Optional<Race> delete(Integer id) {
        logger.traceEntry("delete Race with id {}", id);
        Optional<Race> raceEntity = find(id);
        if (raceEntity.isPresent()) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM Races WHERE race_id = ?")) {
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Race with id {} deleted", id);
                    return raceEntity;
                }
            } catch (SQLException e) {
                logger.error(e);
                System.err.println("Error in delete: " + e.getMessage());
            }
        }
        logger.traceExit();
        return Optional.empty();
    }
}
