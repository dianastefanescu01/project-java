package org.example.repository.hibernate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.User;
import org.example.model.hibernate.UserHibernate;
import org.example.repository.IUserRepository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class UserRepositoryHibernate implements IUserRepository {
    private static final Logger logger = LogManager.getLogger(UserRepositoryHibernate.class);
    private final SessionFactory sessionFactory;

    public UserRepositoryHibernate(Properties props) {
        logger.info("Initializing UserRepositoryHibernate with properties");
        this.sessionFactory = HibernateUtils.getSessionFactory(props);
    }

    private User mapToModel(UserHibernate hUser) {
        if (hUser == null) return null;
        User user = new User(hUser.getName(), hUser.getEmail(), hUser.getPassword());
        user.setId(hUser.getId());
        return user;
    }

    private UserHibernate mapToHibernate(User user) {
        if (user == null) return null;
        UserHibernate hUser = new UserHibernate();
        hUser.setId(user.getId());
        hUser.setName(user.getName());
        hUser.setEmail(user.getEmail());
        hUser.setPassword(user.getPassword());
        return hUser;
    }

    @Override
    public Optional<User> find(Integer id) {
        logger.traceEntry("Finding user with ID: {}", id);
        try (Session session = sessionFactory.openSession()) {
            UserHibernate hUser = session.get(UserHibernate.class, id);
            return Optional.ofNullable(mapToModel(hUser));
        } catch (Exception e) {
            logger.error("Error finding user", e);
            return Optional.empty();
        }
    }

    @Override
    public Iterable<User> findAll() {
        logger.traceEntry("Finding all users");
        List<User> result = new ArrayList<>();
        try (Session session = sessionFactory.openSession()) {
            List<UserHibernate> hUsers = session.createQuery("from UserHibernate", UserHibernate.class).list();
            for (UserHibernate h : hUsers) {
                result.add(mapToModel(h));
            }
            return result;
        } catch (Exception e) {
            logger.error("Error finding all users", e);
            return result;
        }
    }

    @Override
    public Optional<User> save(User entity) {
        logger.traceEntry("Saving user: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            UserHibernate hUser = mapToHibernate(entity);
            Integer id = (Integer) session.save(hUser);
            tx.commit();
            entity.setId(id);
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error saving user", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> delete(Integer id) {
        logger.traceEntry("Deleting user with ID: {}", id);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            UserHibernate hUser = session.get(UserHibernate.class, id);
            if (hUser == null) return Optional.empty();
            tx = session.beginTransaction();
            session.delete(hUser);
            tx.commit();
            return Optional.of(mapToModel(hUser));
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error deleting user", e);
            return Optional.empty();
        }
    }

    @Override
    public Optional<User> update(User entity) {
        logger.traceEntry("Updating user: {}", entity);
        Transaction tx = null;
        try (Session session = sessionFactory.openSession()) {
            tx = session.beginTransaction();
            session.update(mapToHibernate(entity));
            tx.commit();
            return Optional.of(entity);
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            logger.error("Error updating user", e);
            return Optional.empty();
        }
    }

    @Override
    public User getUserByEmailAndPass(String email, String password) {
        logger.traceEntry("Getting user by email and password");
        try (Session session = sessionFactory.openSession()) {
            Query<UserHibernate> query = session.createQuery(
                    "FROM UserHibernate WHERE email = :email AND password = :password", UserHibernate.class);
            query.setParameter("email", email);
            query.setParameter("password", password);
            UserHibernate hUser = query.uniqueResult();
            return mapToModel(hUser);
        } catch (Exception e) {
            logger.error("Error getting user by email and password", e);
            return null;
        }
    }

}
