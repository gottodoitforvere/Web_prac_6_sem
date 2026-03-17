package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.theater.model.Play;

import java.time.LocalDate;
import java.util.List;

public class PlayDao extends AbstractDao<Play> {
    
    public PlayDao() {
        super(Play.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByTheaterId(Long theaterId) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "FROM Play p WHERE p.theater.id = :theaterId"
            );
            query.setParameter("theaterId", theaterId);
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByDirectorId(Long directorId) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "FROM Play p WHERE p.director.id = :directorId"
            );
            query.setParameter("directorId", directorId);
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByActorId(Long actorId) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "SELECT DISTINCT p FROM Play p JOIN p.actors a WHERE a.id = :actorId"
            );
            query.setParameter("actorId", actorId);
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByTitle(String title) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "FROM Play p WHERE LOWER(p.title) LIKE LOWER(:title)"
            );
            query.setParameter("title", "%" + title + "%");
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findBySessionDate(LocalDate date) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "SELECT DISTINCT p FROM Play p JOIN p.sessions s WHERE s.sessionDate = :date"
            );
            query.setParameter("date", date);
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByMaxPriceParterre(int maxPrice) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Play> query = session.createQuery(
                "FROM Play p WHERE p.priceParterre <= :maxPrice"
            );
            query.setParameter("maxPrice", maxPrice);
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
    
    public Play findByIdWithDetails(Long id) {
        Transaction tx = null;
        Play play = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            
            Query<Play> query = session.createQuery(
                "SELECT DISTINCT p FROM Play p " +
                "LEFT JOIN FETCH p.actors " +
                "LEFT JOIN FETCH p.theater " +
                "LEFT JOIN FETCH p.director " +
                "WHERE p.id = :id",
                Play.class
            );
            query.setParameter("id", id);
            play = query.uniqueResult();
            
            if (play != null) {
                Query<Play> sessionsQuery = session.createQuery(
                    "SELECT DISTINCT p FROM Play p " +
                    "LEFT JOIN FETCH p.sessions " +
                    "WHERE p.id = :id",
                    Play.class
                );
                sessionsQuery.setParameter("id", id);
                sessionsQuery.uniqueResult();
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return play;
    }
    
    @SuppressWarnings("unchecked")
    public List<Play> findByFilters(Long theaterId, Long directorId, Long actorId, LocalDate date) {
        Transaction tx = null;
        List<Play> plays;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            
            StringBuilder hql = new StringBuilder("SELECT DISTINCT p FROM Play p ");
            
            if (actorId != null) {
                hql.append("JOIN p.actors a ");
            }
            if (date != null) {
                hql.append("JOIN p.sessions s ");
            }
            
            hql.append("WHERE 1=1 ");
            
            if (theaterId != null) {
                hql.append("AND p.theater.id = :theaterId ");
            }
            if (directorId != null) {
                hql.append("AND p.director.id = :directorId ");
            }
            if (actorId != null) {
                hql.append("AND a.id = :actorId ");
            }
            if (date != null) {
                hql.append("AND s.sessionDate = :date ");
            }
            
            Query<Play> query = session.createQuery(hql.toString());
            
            if (theaterId != null) {
                query.setParameter("theaterId", theaterId);
            }
            if (directorId != null) {
                query.setParameter("directorId", directorId);
            }
            if (actorId != null) {
                query.setParameter("actorId", actorId);
            }
            if (date != null) {
                query.setParameter("date", date);
            }
            
            plays = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return plays;
    }
}
