package org.example.repository.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Race;
import org.example.model.hibernate.RaceHibernate;
import org.example.repository.IRaceRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class RaceRepositoryHibernate implements IRaceRepository {
    private static final Logger logger = LogManager.getLogger(RaceRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public RaceRepositoryHibernate(Properties props) {
        this.sessionFactory =  HibernateUtils.getSessionFactory(props);;
    }

    private Race mapToModel(RaceHibernate h) {
        Race r = new Race(h.getDistance(), h.getStyle(), h.getNrOfParticipants());
        r.setId(h.getId());
        return r;
    }

    private RaceHibernate mapToEntity(Race r) {
        RaceHibernate h = new RaceHibernate(r.getDistance(), r.getStyle(), r.getNrOfParticipants());
        h.setId(r.getId());
        return h;
    }

    @Override
    public Optional<Race> find(Integer id) {
        logger.traceEntry("find Race with id {}", id);
        try (Session session = sessionFactory.openSession()) {
            RaceHibernate h = session.get(RaceHibernate.class, id);
            return h != null ? Optional.of(mapToModel(h)) : Optional.empty();
        } catch (Exception e) {
            logger.error("Error in find", e);
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Race> findAll() {
        logger.traceEntry("findAll Races");
        List<Race> races = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<RaceHibernate> result = session.createQuery("FROM RaceHibernate", RaceHibernate.class).list();
            for (RaceHibernate h : result) {
                races.add(mapToModel(h));
            }
        } catch (Exception e) {
            logger.error("Error in findAll", e);
        }
        return races;
    }

    @Override
    public Optional<Race> save(Race entity) {
        logger.traceEntry("save Race {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            RaceHibernate h = mapToEntity(entity);
            session.persist(h);
            tx.commit();
            entity.setId(h.getId());  // Set generated ID
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in save", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Race> update(Race entity) {
        logger.traceEntry("update Race {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            RaceHibernate h = mapToEntity(entity);
            session.merge(h);
            tx.commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in update", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Race> delete(Integer id) {
        logger.traceEntry("delete Race with id {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            RaceHibernate h = session.get(RaceHibernate.class, id);
            if (h == null) return Optional.empty();
            tx = session.beginTransaction();
            session.remove(h);
            tx.commit();
            return Optional.of(mapToModel(h));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in delete", e);
            return Optional.empty();
        }
    }
}
