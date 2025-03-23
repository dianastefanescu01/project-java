package org.example.repository;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.utils.JdbcUtils;

import java.sql.*;
import java.util.HashMap;
import java.util.Optional;
import java.util.Properties;

public class ParticipantRaceRepository implements IParticipantRaceRepository{
    private JdbcUtils dbUtils;
    private static final Logger logger = LogManager.getLogger();
    private ParticipantRepository participantRepository;
    private RaceRepository raceRepository;

    public ParticipantRaceRepository(Properties props){
        logger.info("Initializing ParticipantRaceRepository with properties: {} ", props);
        dbUtils = new JdbcUtils(props);
        this.participantRepository = new ParticipantRepository(props);
        this.raceRepository = new RaceRepository(props);
    }

    @Override
    public Optional<ParticipantRace> find(Integer id) {
        logger.traceEntry("find Registration with id {}", id);
        ParticipantRace participantRace = null;
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ParticipantRaces WHERE pr_id = ?")) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Participant participant = participantRepository.find(resultSet.getInt("participant_id")).orElse(null);
                Race raceEntity = raceRepository.find(resultSet.getInt("race_id")).orElse(null);
                participantRace = new ParticipantRace(participant, raceEntity);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in find: " + e.getMessage());
        }
        logger.traceExit();
        return Optional.ofNullable(participantRace);
    }

    @Override
    public Iterable<ParticipantRace> findAll() {
        logger.traceEntry("findAll Donations");
        HashMap<Integer, ParticipantRace> participantRaceHashMap = new HashMap<>();
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM ParticipantRaces")) {
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Participant participant = participantRepository.find(resultSet.getInt("participant_id")).orElse(null);
                Race raceEntity = raceRepository.find(resultSet.getInt("race_id")).orElse(null);
                ParticipantRace participantRace = new ParticipantRace(participant, raceEntity);
                participantRaceHashMap.put(participantRace.getID(), participantRace);
            }
        } catch (SQLException e) {
            logger.error(e);
            System.err.println("Error in findAll: " + e.getMessage());
        }
        logger.traceExit();
        return participantRaceHashMap.values();
    }

    @Override
    public Optional<ParticipantRace> save(ParticipantRace entity) {
        logger.traceEntry("save Registration {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "INSERT INTO ParticipantRaces (participant_id, race_id) VALUES (?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, entity.getParticipantId().getID());
            preparedStatement.setInt(2, entity.getRaceId().getID());
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
    public Optional<ParticipantRace> delete(Integer id) {
        logger.traceEntry("delete Registration with id {}", id);
        Optional<ParticipantRace> prEntity = find(id);
        if (prEntity.isPresent()) {
            Connection conn = dbUtils.getConnection();
            try (PreparedStatement preparedStatement = conn.prepareStatement("DELETE FROM ParticipantRaces WHERE pr_id = ?")) {
                preparedStatement.setInt(1, id);
                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    logger.info("Registration with id {} deleted", id);
                    return prEntity;
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
    public Optional<ParticipantRace> update(ParticipantRace entity) {
        logger.traceEntry("update Registration {}", entity);
        Connection conn = dbUtils.getConnection();
        try (PreparedStatement preparedStatement = conn.prepareStatement(
                "UPDATE ParticipantRaces SET participant_id = ?, race_id = ? WHERE pr_id = ?")) {
            preparedStatement.setInt(1, entity.getParticipantId().getID());
            preparedStatement.setInt(2, entity.getRaceId().getID());
            preparedStatement.setInt(3, entity.getID());
            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                logger.info("Registration with id {} updated", entity.getID());
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
