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

public class PlayDaoTest extends BaseTest {
    
    private PlayDao playDao;
    private TheaterDao theaterDao;
    private PersonDao personDao;
    private SessionDao sessionDao;
    
    private Theater theater;
    private Person director;
    private Person actor1;
    private Person actor2;
    
    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        playDao = new PlayDao();
        theaterDao = new TheaterDao();
        personDao = new PersonDao();
        sessionDao = new SessionDao();
        
        theater = new Theater("Тестовый театр", "Тестовый адрес", 100, 50, 30);
        theaterDao.save(theater);
        
        director = new Person("Режиссёр Тестовый", PersonRole.DIRECTOR);
        personDao.save(director);
        
        actor1 = new Person("Актёр Первый", PersonRole.ACTOR);
        personDao.save(actor1);
        
        actor2 = new Person("Актёр Второй", PersonRole.ACTOR);
        personDao.save(actor2);
    }
    
    @Test
    public void testSave() {
        Play play = new Play("Тестовый спектакль", theater, director, 120, 1000, 800, 600);
        
        Play saved = playDao.save(play);
        
        assertNotNull(saved.getId());
        assertEquals(saved.getTitle(), "Тестовый спектакль");
        assertEquals(saved.getDurationMinutes(), Integer.valueOf(120));
        assertEquals(saved.getPriceParterre(), Integer.valueOf(1000));
    }
    
    @Test
    public void testFindById_Exists() {
        Play play = new Play("Гамлет", theater, director, 180, 2000, 1500, 1000);
        playDao.save(play);
        Long id = play.getId();
        
        Play found = playDao.findById(id);
        
        assertNotNull(found);
        assertEquals(found.getId(), id);
        assertEquals(found.getTitle(), "Гамлет");
    }
    
    @Test
    public void testFindById_NotExists() {
        Play found = playDao.findById(999L);
        
        assertNull(found);
    }
    
    @Test
    public void testFindAll() {
        playDao.save(new Play("Спектакль 1", theater, director, 120, 1000, 800, 600));
        playDao.save(new Play("Спектакль 2", theater, director, 150, 1200, 900, 700));
        playDao.save(new Play("Спектакль 3", theater, director, 100, 800, 600, 400));
        
        List<Play> plays = playDao.findAll();
        
        assertNotNull(plays);
        assertEquals(plays.size(), 3);
    }
    
    @Test
    public void testFindByTheaterId() {
        Theater theater2 = new Theater("Другой театр", "Другой адрес", 200, 100, 50);
        theaterDao.save(theater2);
        
        playDao.save(new Play("Спектакль 1", theater, director, 120, 1000, 800, 600));
        playDao.save(new Play("Спектакль 2", theater, director, 150, 1200, 900, 700));
        playDao.save(new Play("Спектакль 3", theater2, director, 100, 800, 600, 400));
        
        List<Play> plays = playDao.findByTheaterId(theater.getId());
        
        assertNotNull(plays);
        assertEquals(plays.size(), 2);
    }
    
    @Test
    public void testFindByDirectorId() {
        Person director2 = new Person("Другой режиссёр", PersonRole.DIRECTOR);
        personDao.save(director2);
        
        playDao.save(new Play("Спектакль 1", theater, director, 120, 1000, 800, 600));
        playDao.save(new Play("Спектакль 2", theater, director, 150, 1200, 900, 700));
        playDao.save(new Play("Спектакль 3", theater, director2, 100, 800, 600, 400));
        
        List<Play> plays = playDao.findByDirectorId(director.getId());
        
        assertNotNull(plays);
        assertEquals(plays.size(), 2);
    }
    
    @Test
    public void testFindByActorId() {
        Play play1 = new Play("Спектакль 1", theater, director, 120, 1000, 800, 600);
        play1.addActor(actor1);
        playDao.save(play1);
        
        Play play2 = new Play("Спектакль 2", theater, director, 150, 1200, 900, 700);
        play2.addActor(actor1);
        play2.addActor(actor2);
        playDao.save(play2);
        
        Play play3 = new Play("Спектакль 3", theater, director, 100, 800, 600, 400);
        play3.addActor(actor2);
        playDao.save(play3);
        
        List<Play> plays = playDao.findByActorId(actor1.getId());
        
        assertNotNull(plays);
        assertEquals(plays.size(), 2);
    }
    
    @Test
    public void testFindByTitle() {
        playDao.save(new Play("Ромео и Джульетта", theater, director, 120, 1000, 800, 600));
        playDao.save(new Play("Гамлет", theater, director, 150, 1200, 900, 700));
        
        List<Play> plays = playDao.findByTitle("ромео");
        
        assertNotNull(plays);
        assertEquals(plays.size(), 1);
        assertEquals(plays.get(0).getTitle(), "Ромео и Джульетта");
    }
    
    @Test
    public void testFindBySessionDate() {
        Play play1 = new Play("Спектакль 1", theater, director, 120, 1000, 800, 600);
        playDao.save(play1);
        
        Play play2 = new Play("Спектакль 2", theater, director, 150, 1200, 900, 700);
        playDao.save(play2);
        
        ru.theater.model.Session session1 = new ru.theater.model.Session(
            play1, LocalDate.of(2024, 3, 15), LocalTime.of(19, 0), 100, 50, 30
        );
        sessionDao.save(session1);
        
        ru.theater.model.Session session2 = new ru.theater.model.Session(
            play2, LocalDate.of(2024, 3, 15), LocalTime.of(20, 0), 100, 50, 30
        );
        sessionDao.save(session2);
        
        List<Play> plays = playDao.findBySessionDate(LocalDate.of(2024, 3, 15));
        
        assertNotNull(plays);
        assertEquals(plays.size(), 2);
    }
    
    @Test
    public void testFindByMaxPriceParterre() {
        playDao.save(new Play("Дешёвый", theater, director, 120, 500, 400, 300));
        playDao.save(new Play("Средний", theater, director, 150, 1000, 800, 600));
        playDao.save(new Play("Дорогой", theater, director, 100, 2000, 1500, 1000));
        
        List<Play> plays = playDao.findByMaxPriceParterre(1000);
        
        assertNotNull(plays);
        assertEquals(plays.size(), 2);
    }
    
    @Test
    public void testGetFormattedDuration() {
        Play play1 = new Play("Спектакль", theater, director, 150, 1000, 800, 600);
        Play play2 = new Play("Спектакль", theater, director, 120, 1000, 800, 600);
        Play play3 = new Play("Спектакль", theater, director, 90, 1000, 800, 600);
        
        assertEquals(play1.getFormattedDuration(), "2 ч 30 мин");
        assertEquals(play2.getFormattedDuration(), "2 ч");
        assertEquals(play3.getFormattedDuration(), "1 ч 30 мин");
    }
    
    @Test
    public void testAddRemoveActor() {
        Play play = new Play("Спектакль", theater, director, 120, 1000, 800, 600);
        
        play.addActor(actor1);
        assertTrue(play.getActors().contains(actor1));
        assertEquals(play.getActors().size(), 1);
        
        play.addActor(actor2);
        assertEquals(play.getActors().size(), 2);
        
        play.removeActor(actor1);
        assertFalse(play.getActors().contains(actor1));
        assertEquals(play.getActors().size(), 1);
    }
}
