package ru.theater.dao;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.theater.model.Person;
import ru.theater.model.PersonRole;
import ru.theater.model.Play;
import ru.theater.model.Theater;

import java.util.List;

import static org.testng.Assert.*;

public class PersonDaoTest extends BaseTest {
    
    private PersonDao personDao;
    
    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        personDao = new PersonDao();
    }
    
    @Test
    public void testSave() {
        Person person = new Person("Иван Иванов", PersonRole.ACTOR);
        
        Person saved = personDao.save(person);
        
        assertNotNull(saved.getId());
        assertEquals(saved.getName(), "Иван Иванов");
        assertEquals(saved.getRole(), PersonRole.ACTOR);
    }
    
    @Test
    public void testFindById_Exists() {
        Person person = new Person("Петр Петров", PersonRole.DIRECTOR);
        personDao.save(person);
        Long id = person.getId();
        
        Person found = personDao.findById(id);
        
        assertNotNull(found);
        assertEquals(found.getId(), id);
        assertEquals(found.getName(), "Петр Петров");
        assertEquals(found.getRole(), PersonRole.DIRECTOR);
    }
    
    @Test
    public void testFindById_NotExists() {
        Person found = personDao.findById(999L);
        
        assertNull(found);
    }
    
    @Test
    public void testFindAll() {
        personDao.save(new Person("Персона 1", PersonRole.ACTOR));
        personDao.save(new Person("Персона 2", PersonRole.DIRECTOR));
        personDao.save(new Person("Персона 3", PersonRole.BOTH));
        
        List<Person> persons = personDao.findAll();
        
        assertNotNull(persons);
        assertEquals(persons.size(), 3);
        assertTrue(persons.stream().anyMatch(p -> p.getName().equals("Персона 1")));
        assertTrue(persons.stream().anyMatch(p -> p.getName().equals("Персона 2")));
        assertTrue(persons.stream().anyMatch(p -> p.getName().equals("Персона 3")));
    }
    
    @Test
    public void testFindByName_Found() {
        personDao.save(new Person("Александр Иванов", PersonRole.ACTOR));
        personDao.save(new Person("Мария Петрова", PersonRole.DIRECTOR));
        
        List<Person> persons = personDao.findByName("александр");
        
        assertNotNull(persons);
        assertEquals(persons.size(), 1);
        assertEquals(persons.get(0).getName(), "Александр Иванов");
        assertEquals(persons.get(0).getRole(), PersonRole.ACTOR);
    }
    
    @Test
    public void testFindByName_NotFound() {
        personDao.save(new Person("Иван", PersonRole.ACTOR));
        
        List<Person> persons = personDao.findByName("Несуществующий");
        
        assertNotNull(persons);
        assertTrue(persons.isEmpty());
    }
    
    @Test
    public void testFindByRole_Found() {
        personDao.save(new Person("Актёр 1", PersonRole.ACTOR));
        personDao.save(new Person("Режиссёр 1", PersonRole.DIRECTOR));
        personDao.save(new Person("Актёр 2", PersonRole.ACTOR));
        
        List<Person> actors = personDao.findByRole(PersonRole.ACTOR);
        
        assertNotNull(actors);
        assertEquals(actors.size(), 2);
        assertTrue(actors.stream().allMatch(p -> p.getRole() == PersonRole.ACTOR));
    }
    
    @Test
    public void testFindByRole_NotFound() {
        personDao.save(new Person("Актёр", PersonRole.ACTOR));
        
        List<Person> persons = personDao.findByRole(PersonRole.BOTH);
        
        assertNotNull(persons);
        assertTrue(persons.isEmpty());
    }
    
    @Test
    public void testFindAllDirectors() {
        personDao.save(new Person("Актёр", PersonRole.ACTOR));
        personDao.save(new Person("Режиссёр", PersonRole.DIRECTOR));
        personDao.save(new Person("Универсал", PersonRole.BOTH));
        
        List<Person> directors = personDao.findAllDirectors();
        
        assertNotNull(directors);
        assertEquals(directors.size(), 2);
        assertTrue(directors.stream().allMatch(Person::canBeDirector));
        assertTrue(directors.stream().anyMatch(p -> p.getRole() == PersonRole.DIRECTOR));
        assertTrue(directors.stream().anyMatch(p -> p.getRole() == PersonRole.BOTH));
    }
    
    @Test
    public void testFindAllDirectors_Empty() {
        personDao.save(new Person("Актёр", PersonRole.ACTOR));
        
        List<Person> directors = personDao.findAllDirectors();
        
        assertNotNull(directors);
        assertTrue(directors.isEmpty());
    }
    
    @Test
    public void testFindAllActors() {
        personDao.save(new Person("Актёр", PersonRole.ACTOR));
        personDao.save(new Person("Режиссёр", PersonRole.DIRECTOR));
        personDao.save(new Person("Универсал", PersonRole.BOTH));
        
        List<Person> actors = personDao.findAllActors();
        
        assertNotNull(actors);
        assertEquals(actors.size(), 2);
        assertTrue(actors.stream().allMatch(Person::canBeActor));
        assertTrue(actors.stream().anyMatch(p -> p.getRole() == PersonRole.ACTOR));
        assertTrue(actors.stream().anyMatch(p -> p.getRole() == PersonRole.BOTH));
    }
    
    @Test
    public void testFindAllActors_Empty() {
        personDao.save(new Person("Режиссёр", PersonRole.DIRECTOR));
        
        List<Person> actors = personDao.findAllActors();
        
        assertNotNull(actors);
        assertTrue(actors.isEmpty());
    }
    
    @Test
    public void testIsPersonInUse_NotUsed() {
        Person person = new Person("Свободный", PersonRole.ACTOR);
        personDao.save(person);
        
        boolean inUse = personDao.isPersonInUse(person.getId());
        
        assertFalse(inUse);
    }
    
    @Test
    public void testIsPersonInUse_AsDirector() {
        TheaterDao theaterDao = new TheaterDao();
        PlayDao playDao = new PlayDao();
        
        Theater theater = new Theater("Театр", "Адрес", 100, 50, 30);
        theaterDao.save(theater);
        
        Person director = new Person("Режиссёр", PersonRole.DIRECTOR);
        personDao.save(director);
        
        Play play = new Play("Спектакль", theater, director, 120, 1000, 800, 600);
        playDao.save(play);
        
        boolean inUse = personDao.isPersonInUse(director.getId());
        
        assertTrue(inUse);
    }
    
    @Test
    public void testIsPersonInUse_AsActor() {
        TheaterDao theaterDao = new TheaterDao();
        PlayDao playDao = new PlayDao();
        
        Theater theater = new Theater("Театр", "Адрес", 100, 50, 30);
        theaterDao.save(theater);
        
        Person director = new Person("Режиссёр", PersonRole.DIRECTOR);
        Person actor = new Person("Актёр", PersonRole.ACTOR);
        personDao.save(director);
        personDao.save(actor);
        
        Play play = new Play("Спектакль", theater, director, 120, 1000, 800, 600);
        play.addActor(actor);
        playDao.save(play);
        
        boolean inUse = personDao.isPersonInUse(actor.getId());
        
        assertTrue(inUse);
    }
    
    @Test
    public void testCanBeDirector() {
        Person director = new Person("Режиссёр", PersonRole.DIRECTOR);
        Person actor = new Person("Актёр", PersonRole.ACTOR);
        Person both = new Person("Универсал", PersonRole.BOTH);
        
        assertTrue(director.canBeDirector());
        assertFalse(actor.canBeDirector());
        assertTrue(both.canBeDirector());
    }
    
    @Test
    public void testCanBeActor() {
        Person director = new Person("Режиссёр", PersonRole.DIRECTOR);
        Person actor = new Person("Актёр", PersonRole.ACTOR);
        Person both = new Person("Универсал", PersonRole.BOTH);
        
        assertFalse(director.canBeActor());
        assertTrue(actor.canBeActor());
        assertTrue(both.canBeActor());
    }
}
