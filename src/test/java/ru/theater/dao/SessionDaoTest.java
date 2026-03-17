package ru.theater.dao;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.theater.model.Person;
import ru.theater.model.PersonRole;
import ru.theater.model.Play;
import ru.theater.model.Theater;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.testng.Assert.*;

public class SessionDaoTest extends BaseTest {
    
    private SessionDao sessionDao;
    private PlayDao playDao;
    private TheaterDao theaterDao;
    private PersonDao personDao;
    
    private Theater theater;
    private Person director;
    private Play play;
    
    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        sessionDao = new SessionDao();
        playDao = new PlayDao();
        theaterDao = new TheaterDao();
        personDao = new PersonDao();
        
        theater = new Theater("Театр", "Адрес", 100, 50, 30);
        theaterDao.save(theater);
        
        director = new Person("Режиссёр", PersonRole.DIRECTOR);
        personDao.save(director);
        
        play = new Play("Спектакль", theater, director, 120, 1000, 800, 600);
        playDao.save(play);
    }
    
    @Test
    public void testSave() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        
        ru.theater.model.Session saved = sessionDao.save(session);
        
        assertNotNull(saved.getId());
        assertEquals(saved.getSessionDate(), LocalDate.of(2024, 3, 15));
        assertEquals(saved.getSessionTime(), LocalTime.of(19, 0));
        assertEquals(saved.getFreeParterre(), Integer.valueOf(100));
    }
    
    @Test
    public void testFindById_Exists() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        Long id = session.getId();
        
        ru.theater.model.Session found = sessionDao.findById(id);
        
        assertNotNull(found);
        assertEquals(found.getId(), id);
    }
    
    @Test
    public void testFindById_NotExists() {
        ru.theater.model.Session found = sessionDao.findById(999L);
        
        assertNull(found);
    }
    
    @Test
    public void testFindAll() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findAll();
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 3);
    }
    
    @Test
    public void testFindByPlayId() {
        Play play2 = new Play("Другой спектакль", theater, director, 150, 1200, 900, 700);
        playDao.save(play2);
        
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play2, LocalDate.of(2024, 3, 15), LocalTime.of(20, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayId(play.getId());
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
    }
    
    @Test
    public void testFindByDate() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(20, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDate(LocalDate.of(2024, 3, 15));
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
    }
    
    @Test
    public void testFindByDateRange() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 14), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDateRange(
            LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)
        );
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
    }
    
    @Test
    public void testFindWithAvailableSeats() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 10, 5, 3));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 50, 25, 15));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findWithAvailableSeats("parterre", 20);
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
    }
    
    @Test
    public void testFindUpcoming() {
        LocalDate today = LocalDate.now();
        
        sessionDao.save(new ru.theater.model.Session(play, today.minusDays(1), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, today, LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, today.plusDays(1), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findUpcoming();
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2, "Должно быть найдено 2 будущих сеанса (включая сегодняшний)");
    }
    
    @Test
    public void testFindByPlayIdWithAvailableSeats() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 0, 0, 0));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 10, 0, 0));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 0, 5, 0));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayIdWithAvailableSeats(play.getId());
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2, "Должно быть 2 сеанса со свободными местами");
    }
    
    @Test
    public void testBuyTickets_Success() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "parterre", 10);
        
        assertTrue(result, "Покупка должна быть успешной");
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeParterre(), Integer.valueOf(90));
    }
    
    @Test
    public void testBuyTickets_NotEnoughSeats() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 5, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "parterre", 10);
        
        assertFalse(result, "Покупка должна быть неуспешной");
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeParterre(), Integer.valueOf(5), "Количество мест не должно измениться");
    }
    
    @Test
    public void testGetTotalFreeSeats() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        
        Integer total = session.getTotalFreeSeats();
        
        assertEquals(total, Integer.valueOf(180));
    }
    
    @Test
    public void testHasAvailableSeats() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 10, 5, 3
        );
        
        assertTrue(session.hasAvailableSeats("parterre", 5));
        assertTrue(session.hasAvailableSeats("parterre", 10));
        assertFalse(session.hasAvailableSeats("parterre", 11));
        
        assertTrue(session.hasAvailableSeats("balcony", 5));
        assertFalse(session.hasAvailableSeats("balcony", 6));
    }
}
