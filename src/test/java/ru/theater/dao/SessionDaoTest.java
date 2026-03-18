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
        assertEquals(saved.getPlay().getId(), play.getId());
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
        assertEquals(found.getPlay().getId(), play.getId());
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
        assertTrue(sessions.stream().allMatch(s -> s.getPlay().getId().equals(play.getId())));
    }
    
    @Test
    public void testFindByPlayId_Found() {
        Play play2 = new Play("Другой спектакль", theater, director, 150, 1200, 900, 700);
        playDao.save(play2);
        
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play2, LocalDate.of(2024, 3, 15), LocalTime.of(20, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayId(play.getId());
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s -> s.getPlay().getId().equals(play.getId())));
        assertTrue(!sessions.get(0).getSessionDate().isAfter(sessions.get(1).getSessionDate()));
    }
    
    @Test
    public void testFindByPlayId_NotFound() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayId(999L);
        
        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }
    
    @Test
    public void testFindByDate_Found() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(20, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDate(LocalDate.of(2024, 3, 15));
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s -> s.getSessionDate().equals(LocalDate.of(2024, 3, 15))));
        assertTrue(!sessions.get(0).getSessionTime().isAfter(sessions.get(1).getSessionTime()));
    }
    
    @Test
    public void testFindByDate_NotFound() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDate(LocalDate.of(2099, 1, 1));
        
        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }
    
    @Test
    public void testFindByDateRange_Found() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 14), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDateRange(
            LocalDate.of(2024, 3, 15), LocalDate.of(2024, 3, 16)
        );
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s ->
            !s.getSessionDate().isBefore(LocalDate.of(2024, 3, 15)) &&
            !s.getSessionDate().isAfter(LocalDate.of(2024, 3, 16))
        ));
    }
    
    @Test
    public void testFindByDateRange_NotFound() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 20), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByDateRange(
            LocalDate.of(2024, 3, 1), LocalDate.of(2024, 3, 5)
        );
        
        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }
    
    @Test
    public void testFindWithAvailableSeats_Parterre() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 10, 5, 3));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 50, 25, 15));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findWithAvailableSeats("parterre", 20);
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s -> s.getFreeParterre() >= 20));
    }
    
    @Test
    public void testFindWithAvailableSeats_Balcony() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 0, 10, 0));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 0, 30, 0));
        
        List<ru.theater.model.Session> sessions = sessionDao.findWithAvailableSeats("balcony", 20);
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 1);
        assertTrue(sessions.stream().allMatch(s -> s.getFreeBalcony() >= 20));
    }
    
    @Test
    public void testFindWithAvailableSeats_Mezzanine() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 0, 0, 10));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 0, 0, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findWithAvailableSeats("mezzanine", 20);
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 1);
        assertTrue(sessions.stream().allMatch(s -> s.getFreeMezzanine() >= 20));
    }
    
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testFindWithAvailableSeats_InvalidType() {
        sessionDao.findWithAvailableSeats("invalid", 10);
    }
    
    @Test
    public void testFindUpcoming_Found() {
        LocalDate today = LocalDate.now();
        
        sessionDao.save(new ru.theater.model.Session(play, today.minusDays(1), LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, today, LocalTime.of(19, 0), 100, 50, 30));
        sessionDao.save(new ru.theater.model.Session(play, today.plusDays(1), LocalTime.of(19, 0), 100, 50, 30));
        
        List<ru.theater.model.Session> sessions = sessionDao.findUpcoming();
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s -> !s.getSessionDate().isBefore(today)));
    }
    
    @Test
    public void testFindUpcoming_Empty() {
        List<ru.theater.model.Session> sessions = sessionDao.findUpcoming();
        
        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }
    
    @Test
    public void testFindByPlayIdWithAvailableSeats_Found() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 0, 0, 0));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 16), LocalTime.of(19, 0), 10, 0, 0));
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 17), LocalTime.of(19, 0), 0, 5, 0));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayIdWithAvailableSeats(play.getId());
        
        assertNotNull(sessions);
        assertEquals(sessions.size(), 2);
        assertTrue(sessions.stream().allMatch(s ->
            s.getFreeParterre() > 0 || s.getFreeBalcony() > 0 || s.getFreeMezzanine() > 0
        ));
    }
    
    @Test
    public void testFindByPlayIdWithAvailableSeats_NotFound() {
        sessionDao.save(new ru.theater.model.Session(play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 0, 0, 0));
        
        List<ru.theater.model.Session> sessions = sessionDao.findByPlayIdWithAvailableSeats(play.getId());
        
        assertNotNull(sessions);
        assertTrue(sessions.isEmpty());
    }
    
    @Test
    public void testBuyTickets_Parterre_Success() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "parterre", 10);
        
        assertTrue(result);
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeParterre(), Integer.valueOf(90));
        assertEquals(updated.getFreeBalcony(), Integer.valueOf(50));
        assertEquals(updated.getFreeMezzanine(), Integer.valueOf(30));
    }
    
    @Test
    public void testBuyTickets_Balcony_Success() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "balcony", 10);
        
        assertTrue(result);
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeBalcony(), Integer.valueOf(40));
        assertEquals(updated.getFreeParterre(), Integer.valueOf(100));
        assertEquals(updated.getFreeMezzanine(), Integer.valueOf(30));
    }
    
    @Test
    public void testBuyTickets_Mezzanine_Success() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "mezzanine", 10);
        
        assertTrue(result);
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeMezzanine(), Integer.valueOf(20));
        assertEquals(updated.getFreeParterre(), Integer.valueOf(100));
        assertEquals(updated.getFreeBalcony(), Integer.valueOf(50));
    }
    
    @Test
    public void testBuyTickets_NotEnoughSeats() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 5, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "parterre", 10);
        
        assertFalse(result);
        
        ru.theater.model.Session updated = sessionDao.findById(session.getId());
        assertEquals(updated.getFreeParterre(), Integer.valueOf(5));
    }
    
    @Test
    public void testBuyTickets_SessionNotFound() {
        boolean result = sessionDao.buyTickets(999L, "parterre", 10);
        
        assertFalse(result);
    }
    
    @Test
    public void testBuyTickets_InvalidSeatType() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session);
        
        boolean result = sessionDao.buyTickets(session.getId(), "invalid", 10);
        
        assertFalse(result);
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
        assertTrue(session.hasAvailableSeats("mezzanine", 3));
        assertFalse(session.hasAvailableSeats("mezzanine", 4));
        assertFalse(session.hasAvailableSeats("invalid", 1));
    }
    
    @Test
    public void testSessionBuyTickets() {
        ru.theater.model.Session session = new ru.theater.model.Session(
            play, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 10, 10, 10
        );
        
        assertTrue(session.buyTickets("parterre", 5));
        assertEquals(session.getFreeParterre(), Integer.valueOf(5));
        
        assertTrue(session.buyTickets("balcony", 4));
        assertEquals(session.getFreeBalcony(), Integer.valueOf(6));
        
        assertTrue(session.buyTickets("mezzanine", 3));
        assertEquals(session.getFreeMezzanine(), Integer.valueOf(7));
        
        assertFalse(session.buyTickets("invalid", 1));
        assertFalse(session.buyTickets("parterre", 100));
    }
    
    @Test
    public void testSessionConstructorWithTheaterSeats() {
        Theater localTheater = new Theater("Театр", "Адрес", 120, 80, 40);
        Person localDirector = new Person("Режиссёр", PersonRole.DIRECTOR);
        Play localPlay = new Play("Спектакль", localTheater, localDirector, 120, 1000, 800, 600);
        
        ru.theater.model.Session session = new ru.theater.model.Session(
            localPlay, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0)
        );
        
        assertEquals(session.getPlay(), localPlay);
        assertEquals(session.getFreeParterre(), Integer.valueOf(120));
        assertEquals(session.getFreeBalcony(), Integer.valueOf(80));
        assertEquals(session.getFreeMezzanine(), Integer.valueOf(40));
    }
}
