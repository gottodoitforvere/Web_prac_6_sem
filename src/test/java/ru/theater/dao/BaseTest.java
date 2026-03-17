package ru.theater.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import ru.theater.util.HibernateUtil;

public abstract class BaseTest {
    
    @BeforeClass
    public void setUpClass() {
        System.out.println("=== Начало тестирования класса " + getClass().getSimpleName() + " ===");
    }
    
    @AfterClass
    public void tearDownClass() {
        System.out.println("=== Завершение тестирования класса " + getClass().getSimpleName() + " ===");
    }
    
    @BeforeMethod
    public void setUp() {
        cleanDatabase();
    }
    
    @AfterMethod
    public void tearDown() {
    }
    
    protected void cleanDatabase() {
        Transaction tx = null;
        try {
            Session session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            
            session.createNativeQuery("TRUNCATE TABLE play_actor, session, play, person, theater RESTART IDENTITY CASCADE").executeUpdate();
            
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            throw e;
        }
    }
}
