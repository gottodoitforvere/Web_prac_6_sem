package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.theater.model.Theater;

import java.util.List;

public class TheaterDao extends AbstractDao<Theater> {
    
    public TheaterDao() {
        super(Theater.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<Theater> findByName(String name) {
        Transaction tx = null;
        List<Theater> theaters;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Theater> query = session.createQuery(
                "FROM Theater t WHERE LOWER(t.name) LIKE LOWER(:name)"
            );
            query.setParameter("name", "%" + name + "%");
            theaters = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return theaters;
    }
    
    @SuppressWarnings("unchecked")
    public List<Theater> findByAddress(String address) {
        Transaction tx = null;
        List<Theater> theaters;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Theater> query = session.createQuery(
                "FROM Theater t WHERE LOWER(t.address) LIKE LOWER(:address)"
            );
            query.setParameter("address", "%" + address + "%");
            theaters = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return theaters;
    }
    
    @SuppressWarnings("unchecked")
    public List<Theater> findByMinTotalSeats(int minSeats) {
        Transaction tx = null;
        List<Theater> theaters;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Theater> query = session.createQuery(
                "FROM Theater t WHERE (t.seatsParterre + t.seatsBalcony + t.seatsMezzanine) >= :minSeats"
            );
            query.setParameter("minSeats", minSeats);
            theaters = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return theaters;
    }
    
    public Theater findByIdWithPlays(Long id) {
        Transaction tx = null;
        Theater theater = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Theater> query = session.createQuery(
                "FROM Theater t LEFT JOIN FETCH t.plays WHERE t.id = :id",
                Theater.class
            );
            query.setParameter("id", id);
            theater = query.uniqueResult();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return theater;
    }
}
