package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import ru.theater.model.Person;
import ru.theater.model.PersonRole;

import java.util.List;

public class PersonDao extends AbstractDao<Person> {
    
    public PersonDao() {
        super(Person.class);
    }
    
    @SuppressWarnings("unchecked")
    public List<Person> findByName(String name) {
        Transaction tx = null;
        List<Person> persons;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Person> query = session.createQuery(
                "FROM Person p WHERE LOWER(p.name) LIKE LOWER(:name)"
            );
            query.setParameter("name", "%" + name + "%");
            persons = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return persons;
    }
    
    @SuppressWarnings("unchecked")
    public List<Person> findByRole(PersonRole role) {
        Transaction tx = null;
        List<Person> persons;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Person> query = session.createQuery(
                "FROM Person p WHERE p.role = :role"
            );
            query.setParameter("role", role);
            persons = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return persons;
    }
    
    @SuppressWarnings("unchecked")
    public List<Person> findAllDirectors() {
        Transaction tx = null;
        List<Person> persons;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Person> query = session.createQuery(
                "FROM Person p WHERE p.role = :director OR p.role = :both"
            );
            query.setParameter("director", PersonRole.DIRECTOR);
            query.setParameter("both", PersonRole.BOTH);
            persons = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return persons;
    }
    
    @SuppressWarnings("unchecked")
    public List<Person> findAllActors() {
        Transaction tx = null;
        List<Person> persons;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            Query<Person> query = session.createQuery(
                "FROM Person p WHERE p.role = :actor OR p.role = :both"
            );
            query.setParameter("actor", PersonRole.ACTOR);
            query.setParameter("both", PersonRole.BOTH);
            persons = query.list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return persons;
    }
    
    public boolean isPersonInUse(Long id) {
        Transaction tx = null;
        boolean inUse = false;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            
            Query<Long> directorQuery = session.createQuery(
                "SELECT COUNT(p) FROM Play p WHERE p.director.id = :personId",
                Long.class
            );
            directorQuery.setParameter("personId", id);
            Long directorCount = directorQuery.uniqueResult();
            
            Query<Long> actorQuery = session.createQuery(
                "SELECT COUNT(p) FROM Play p JOIN p.actors a WHERE a.id = :personId",
                Long.class
            );
            actorQuery.setParameter("personId", id);
            Long actorCount = actorQuery.uniqueResult();
            
            inUse = (directorCount > 0) || (actorCount > 0);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return inUse;
    }
}
