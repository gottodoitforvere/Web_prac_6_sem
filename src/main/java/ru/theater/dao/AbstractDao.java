package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import ru.theater.util.HibernateUtil;

import java.util.List;

public abstract class AbstractDao<T> {
    
    private final Class<T> entityClass;
    
    protected AbstractDao(Class<T> entityClass) {
        this.entityClass = entityClass;
    }
    
    protected Session getSession() {
        return HibernateUtil.getSessionFactory().getCurrentSession();
    }
    
    public T findById(Long id) {
        Transaction tx = null;
        T entity = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            entity = session.get(entityClass, id);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    public List<T> findAll() {
        Transaction tx = null;
        List<T> entities;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            entities = session.createQuery("FROM " + entityClass.getSimpleName()).list();
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return entities;
    }
    
    public T save(T entity) {
        Transaction tx = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            session.save(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return entity;
    }
    
    public T update(T entity) {
        Transaction tx = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            session.update(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return entity;
    }
    
    public T saveOrUpdate(T entity) {
        Transaction tx = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            session.saveOrUpdate(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
        return entity;
    }
    
    public void delete(T entity) {
        Transaction tx = null;
        try {
            Session session = getSession();
            tx = session.beginTransaction();
            session.delete(entity);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
    
    public void deleteById(Long id) {
        T entity = findById(id);
        if (entity != null) {
            delete(entity);
        }
    }
}
