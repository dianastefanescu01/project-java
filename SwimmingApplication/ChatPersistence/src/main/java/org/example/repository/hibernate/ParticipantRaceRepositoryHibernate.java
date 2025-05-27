package org.example.repository.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.ParticipantRace;
import org.example.model.Race;
import org.example.model.hibernate.ParticipantRaceHibernate;
import org.example.repository.IParticipantRaceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ParticipantRaceRepositoryHibernate implements IParticipantRaceRepository {
    private static final Logger logger = LogManager.getLogger(ParticipantRaceRepositoryHibernate.class);
    private final SessionFactory sessionFactory;
    private final ParticipantRepositoryHibernate participantRepository;
    private final RaceRepositoryHibernate raceRepository;

    public ParticipantRaceRepositoryHibernate(Properties props) {
        this.sessionFactory =  HibernateUtils.getSessionFactory(props);;
        this.participantRepository = new ParticipantRepositoryHibernate(props);
        this.raceRepository = new RaceRepositoryHibernate(props);
    }

    private ParticipantRace mapToModel(ParticipantRaceHibernate h) {
        Participant participant = participantRepository.find(h.getParticipant().getId()).orElse(null);
        Race race = raceRepository.find(h.getRace().getId()).orElse(null);
        if (participant == null || race == null) return null;

        ParticipantRace pr = new ParticipantRace(participant, race);
        pr.setId(h.getId());
        return pr;
    }

    private ParticipantRaceHibernate mapToEntity(ParticipantRace model) {
        return new ParticipantRaceHibernate(
                model.getId(),
                model.getParticipantId().getId(),
                model.getRaceId().getId()
        );
    }

    @Override
    public Optional<ParticipantRace> find(Integer id) {
        logger.traceEntry("Find registration with id {}", id);
        try (Session session = sessionFactory.openSession()) {
            ParticipantRaceHibernate h = session.get(ParticipantRaceHibernate.class, id);
            if (h == null) return Optional.empty();
            return Optional.ofNullable(mapToModel(h));
        } catch (Exception e) {
            logger.error("Error in find", e);
            return Optional.empty();
        }
    }

    @Override
    public Iterable<ParticipantRace> findAll() {
        logger.traceEntry("findAll registrations");
        List<ParticipantRace> result = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<ParticipantRaceHibernate> list = session.createQuery("FROM ParticipantRaceHibernate", ParticipantRaceHibernate.class).list();
            for (ParticipantRaceHibernate h : list) {
                ParticipantRace pr = mapToModel(h);
                if (pr != null) result.add(pr);
            }
        } catch (Exception e) {
            logger.error("Error in findAll", e);
        }
        return result;
    }

    @Override
    public Optional<ParticipantRace> save(ParticipantRace entity) {
        logger.traceEntry("save registration {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ParticipantRaceHibernate h = mapToEntity(entity);
            session.persist(h);
            tx.commit();
            entity.setId(h.getId());
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in save", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ParticipantRace> delete(Integer id) {
        logger.traceEntry("delete registration with id {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            ParticipantRaceHibernate h = session.get(ParticipantRaceHibernate.class, id);
            if (h == null) return Optional.empty();
            tx = session.beginTransaction();
            session.remove(h);
            tx.commit();
            return Optional.ofNullable(mapToModel(h));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in delete", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<ParticipantRace> update(ParticipantRace entity) {
        logger.traceEntry("update registration {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ParticipantRaceHibernate h = mapToEntity(entity);
            session.merge(h);
            tx.commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in update", e);
            return Optional.empty();
        }
    }
}
