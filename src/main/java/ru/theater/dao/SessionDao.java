package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.time.LocalDate;
import java.util.List;

public class SessionDao extends AbstractDao<ru.theater.model.Session> {
    
    public SessionDao() {
        super(ru.theater.model.Session.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findByPlayId(Long playId) {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s.play.id = :playId ORDER BY s.sessionDate, s.sessionTime"
            );
            query.setParameter("playId", playId);
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findByDate(LocalDate date) {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s.sessionDate = :date ORDER BY s.sessionTime"
            );
            query.setParameter("date", date);
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findByDateRange(LocalDate startDate, LocalDate endDate) {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s.sessionDate BETWEEN :startDate AND :endDate " +
                "ORDER BY s.sessionDate, s.sessionTime"
            );
            query.setParameter("startDate", startDate);
            query.setParameter("endDate", endDate);
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findWithAvailableSeats(String seatType, int minFree) {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            
            String field;
            switch (seatType.toLowerCase()) {
                case "parterre":
                    field = "freeParterre";
                    break;
                case "balcony":
                    field = "freeBalcony";
                    break;
                case "mezzanine":
                    field = "freeMezzanine";
                    break;
                default:
                    throw new IllegalArgumentException("Invalid seat type: " + seatType);
            }
            
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s." + field + " >= :minFree " +
                "ORDER BY s.sessionDate, s.sessionTime"
            );
            query.setParameter("minFree", minFree);
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findUpcoming() {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s.sessionDate >= :today " +
                "ORDER BY s.sessionDate, s.sessionTime"
            );
            query.setParameter("today", LocalDate.now());
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    @SuppressWarnings("unchecked")
    public List<ru.theater.model.Session> findByPlayIdWithAvailableSeats(Long playId) {
        Transaction tx = null;
        List<ru.theater.model.Session> sessions;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<ru.theater.model.Session> query = session.createQuery(
                "FROM Session s WHERE s.play.id = :playId " +
                "AND (s.freeParterre > 0 OR s.freeBalcony > 0 OR s.freeMezzanine > 0) " +
                "ORDER BY s.sessionDate, s.sessionTime"
            );
            query.setParameter("playId", playId);
            sessions = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return sessions;
    }
    
    public boolean buyTickets(Long sessionId, String seatType, int count) {
        Transaction tx = null;
        boolean success = false;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            
            ru.theater.model.Session theaterSession = session.get(ru.theater.model.Session.class, sessionId);
            if (theaterSession != null) {
                success = theaterSession.buyTickets(seatType, count);
                if (success) {
                    session.update(theaterSession);
                }
            }
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return success;
    }
}
