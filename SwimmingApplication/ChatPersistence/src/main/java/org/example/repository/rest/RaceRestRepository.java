package org.example.repository.rest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Race;
import org.example.repository.IRaceRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RaceRestRepository implements IRaceRepository {
    private static final Logger logger = LogManager.getLogger(RaceRestRepository.class);
    private final Connection connection;

    public RaceRestRepository(Properties props) {
        logger.info("Initializing REST Race Repository");
        try {
            String url = props.getProperty("jdbc.url");
            logger.debug("Connecting to database: {}", url);
            connection = DriverManager.getConnection(url);
            logger.info("Connected to database successfully");
        } catch (SQLException e) {
            logger.error("Failed to connect to database", e);
            throw new RuntimeException("Database connection failed", e);
        }
    }

    @Override
    public Optional<Race> find(Integer raceId) {
        logger.debug("Finding race by id={}", raceId);
        String sql = "SELECT * FROM Races WHERE race_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, raceId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Race race = new Race(
                            rs.getInt("distance"),
                            rs.getString("style"),
                            rs.getInt("nrOfParticipants")
                    );
                    race.setId(rs.getInt("race_id"));
                    return Optional.of(race);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding race by id={}", raceId, e);
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Race> findAll() {
        logger.debug("Finding all races");
        List<Race> races = new ArrayList<>();
        String sql = "SELECT * FROM Races";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Race race = new Race(
                        rs.getInt("distance"),
                        rs.getString("style"),
                        rs.getInt("nrOfParticipants")
                );
                race.setId(rs.getInt("race_id"));
                races.add(race);
            }
            logger.info("Found {} races", races.size());
        } catch (SQLException e) {
            logger.error("Error retrieving races", e);
        }
        return races;
    }

    @Override
    public Optional<Race> save(Race race) {
        logger.debug("Saving new race: {}", race);
        String sql = "INSERT INTO Races (style, distance, nrOfParticipants) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, race.getStyle());
            stmt.setInt(2, race.getDistance());
            stmt.setInt(3, race.getNrOfParticipants());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Saving race failed, no rows affected.");
                return Optional.empty();
            }

            /*try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    race.setId(generatedKeys.getInt(1));
                    logger.info("Race created with ID: {}", race.getId());
                    return Optional.of(race);
                }
            }

             */
            logger.info("Race inserted (ID not retrieved)");
            return Optional.of(race);
        } catch (SQLException e) {
            logger.error("Error saving race", e);
        }
        return Optional.empty();
    }

    /*public Optional<Race> save(Race race) {
        logger.debug("Saving new race: {}", race);
        String sql = "INSERT INTO Races (style, distance, nrOfParticipants) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, race.getStyle());
            stmt.setInt(2, race.getDistance());
            stmt.setInt(3, race.getNrOfParticipants());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                logger.error("Saving race failed, no rows affected.");
                return Optional.empty();
            }

            // Skip getGeneratedKeys entirely
            logger.info("Race inserted (ID not retrieved)");
            return Optional.of(race); // race will not have ID, and that's okay
        } catch (SQLException e) {
            logger.error("Error saving race", e);
        }
        return Optional.empty();
    }

     */


    @Override
    public Optional<Race> update(Race race) {
        logger.debug("Updating race: {}", race);
        String sql = "UPDATE Races SET style = ?, distance = ?, nrOfParticipants = ? WHERE race_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, race.getStyle());
            stmt.setInt(2, race.getDistance());
            stmt.setInt(3, race.getNrOfParticipants());
            stmt.setInt(4, race.getId());  // Use id_race column

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                logger.info("Successfully updated race with race_id={}", race.getId());
                return Optional.of(race);
            } else {
                logger.warn("No race found with race_id={} for update", race.getId());
            }
        } catch (SQLException e) {
            logger.error("Error updating race with race_id={}", race.getId(), e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Race> delete(Integer raceId) {
        logger.debug("Deleting race with race_id={}", raceId);
        Optional<Race> race = find(raceId);
        if (race.isEmpty()) {
            logger.warn("No race found to delete with race_id={}", raceId);
            return Optional.empty();
        }

        String sql = "DELETE FROM Races WHERE race_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, raceId);
            int rows = stmt.executeUpdate();
            if (rows > 0) {
                logger.info("Successfully deleted race with race_id={}", raceId);
                return race;
            }
        } catch (SQLException e) {
            logger.error("Error deleting race with race_id={}", raceId, e);
        }
        return Optional.empty();
    }
}
