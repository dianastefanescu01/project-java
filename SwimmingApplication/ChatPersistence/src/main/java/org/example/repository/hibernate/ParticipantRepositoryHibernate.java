package org.example.repository.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Participant;
import org.example.model.hibernate.ParticipantHibernate;
import org.example.repository.IParticipantRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class ParticipantRepositoryHibernate implements IParticipantRepository {
    private static final Logger logger = LogManager.getLogger(ParticipantRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public ParticipantRepositoryHibernate(Properties props) {
        this.sessionFactory =  HibernateUtils.getSessionFactory(props);;
    }

    private Participant mapToModel(ParticipantHibernate h) {
        Participant p = new Participant(h.getName(), h.getAge(), h.getUserId());
        p.setId(h.getId());
        return p;
    }

    private ParticipantHibernate mapToEntity(Participant p) {
        ParticipantHibernate h = new ParticipantHibernate(p.getName(), p.getAge(), p.getUserId());
        h.setId(p.getId());
        return h;
    }

    @Override
    public ArrayList<Participant> findByName(String name) {
        logger.traceEntry("FindParticipantsByName {}", name);
        List<Participant> participants = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<ParticipantHibernate> results = session
                    .createQuery("FROM ParticipantHibernate WHERE name LIKE :name", ParticipantHibernate.class)
                    .setParameter("name", "%" + name + "%")
                    .getResultList();

            for (ParticipantHibernate p : results) {
                participants.add(mapToModel(p));
            }
        } catch (Exception e) {
            logger.error("Error in findByName", e);
        }
        return new ArrayList<>(participants);
    }

    @Override
    public Optional<Participant> find(Integer id) {
        logger.traceEntry("Find Participant with id {}", id);
        try (Session session = sessionFactory.openSession()) {
            ParticipantHibernate h = session.get(ParticipantHibernate.class, id);
            return h != null ? Optional.of(mapToModel(h)) : Optional.empty();
        } catch (Exception e) {
            logger.error("Error in find", e);
            return Optional.empty();
        }
    }

    @Override
    public Iterable<Participant> findAll() {
        logger.traceEntry("findAll Participants");
        List<Participant> participants = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<ParticipantHibernate> results = session.createQuery("FROM ParticipantHibernate", ParticipantHibernate.class).list();
            for (ParticipantHibernate h : results) {
                participants.add(mapToModel(h));
            }
        } catch (Exception e) {
            logger.error("Error in findAll", e);
        }
        return participants;
    }

    @Override
    public Optional<Participant> save(Participant entity) {
        logger.traceEntry("save Participant {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ParticipantHibernate h = mapToEntity(entity);
            session.persist(h);
            tx.commit();
            entity.setId(h.getId());  // auto-generated ID
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error in save", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Participant> delete(Integer id) {
        logger.traceEntry("delete Participant with id {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            ParticipantHibernate h = session.get(ParticipantHibernate.class, id);
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

    @Override
    public Optional<Participant> update(Participant entity) {
        logger.traceEntry("update Participant {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            ParticipantHibernate h = mapToEntity(entity);
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
