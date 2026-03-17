package ru.theater.dao;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import ru.theater.model.Theater;

import java.util.List;

import static org.testng.Assert.*;

public class TheaterDaoTest extends BaseTest {
    
    private TheaterDao theaterDao;
    
    @BeforeMethod
    @Override
    public void setUp() {
        super.setUp();
        theaterDao = new TheaterDao();
    }
    
    @Test
    public void testSave() {
        Theater theater = new Theater("Тестовый театр", "ул. Тестовая, 1", 100, 50, 30);
        
        Theater saved = theaterDao.save(theater);
        
        assertNotNull(saved.getId(), "ID должен быть присвоен после сохранения");
        assertEquals(saved.getName(), "Тестовый театр");
        assertEquals(saved.getAddress(), "ул. Тестовая, 1");
        assertEquals(saved.getSeatsParterre(), Integer.valueOf(100));
        assertEquals(saved.getSeatsBalcony(), Integer.valueOf(50));
        assertEquals(saved.getSeatsMezzanine(), Integer.valueOf(30));
    }
    
    @Test
    public void testFindById_Exists() {
        Theater theater = new Theater("Большой театр", "Москва", 500, 200, 150);
        theaterDao.save(theater);
        Long id = theater.getId();
        
        Theater found = theaterDao.findById(id);
        
        assertNotNull(found, "Театр должен быть найден");
        assertEquals(found.getId(), id);
        assertEquals(found.getName(), "Большой театр");
        assertEquals(found.getAddress(), "Москва");
    }
    
    @Test
    public void testFindById_NotExists() {
        Theater found = theaterDao.findById(999L);
        
        assertNull(found, "Несуществующий театр должен вернуть null");
    }
    
    @Test
    public void testFindAll() {
        theaterDao.save(new Theater("Театр 1", "Адрес 1", 100, 50, 30));
        theaterDao.save(new Theater("Театр 2", "Адрес 2", 200, 100, 60));
        theaterDao.save(new Theater("Театр 3", "Адрес 3", 150, 75, 45));
        
        List<Theater> theaters = theaterDao.findAll();
        
        assertNotNull(theaters);
        assertEquals(theaters.size(), 3, "Должно быть найдено 3 театра");
    }
    
    @Test
    public void testUpdate() {
        Theater theater = new Theater("Старое название", "Старый адрес", 100, 50, 30);
        theaterDao.save(theater);
        
        theater.setName("Новое название");
        theater.setAddress("Новый адрес");
        theaterDao.update(theater);
        
        Theater updated = theaterDao.findById(theater.getId());
        assertEquals(updated.getName(), "Новое название");
        assertEquals(updated.getAddress(), "Новый адрес");
    }
    
    @Test
    public void testDelete() {
        Theater theater = new Theater("Театр для удаления", "Адрес", 100, 50, 30);
        theaterDao.save(theater);
        Long id = theater.getId();
        
        theaterDao.delete(theater);
        
        Theater found = theaterDao.findById(id);
        assertNull(found, "Удалённый театр не должен быть найден");
    }
    
    @Test
    public void testFindByName_Found() {
        theaterDao.save(new Theater("Большой театр", "Москва", 500, 200, 150));
        theaterDao.save(new Theater("Малый театр", "Петербург", 300, 100, 80));
        
        List<Theater> theaters = theaterDao.findByName("большой");
        
        assertNotNull(theaters);
        assertEquals(theaters.size(), 1);
        assertEquals(theaters.get(0).getName(), "Большой театр");
    }
    
    @Test
    public void testFindByName_NotFound() {
        theaterDao.save(new Theater("Большой театр", "Москва", 500, 200, 150));
        
        List<Theater> theaters = theaterDao.findByName("Несуществующий");
        
        assertNotNull(theaters);
        assertTrue(theaters.isEmpty(), "Список должен быть пустым");
    }
    
    @Test
    public void testFindByAddress() {
        theaterDao.save(new Theater("Театр 1", "Москва, ул. Ленина", 100, 50, 30));
        theaterDao.save(new Theater("Театр 2", "Петербург, пр. Невский", 200, 100, 60));
        
        List<Theater> theaters = theaterDao.findByAddress("москва");
        
        assertNotNull(theaters);
        assertEquals(theaters.size(), 1);
        assertTrue(theaters.get(0).getAddress().contains("Москва"));
    }
    
    @Test
    public void testFindByMinTotalSeats() {
        theaterDao.save(new Theater("Малый театр", "Адрес 1", 50, 30, 20));
        theaterDao.save(new Theater("Средний театр", "Адрес 2", 200, 100, 50));
        theaterDao.save(new Theater("Большой театр", "Адрес 3", 500, 200, 150));
        
        List<Theater> theaters = theaterDao.findByMinTotalSeats(300);
        
        assertNotNull(theaters);
        assertEquals(theaters.size(), 2, "Должно быть найдено 2 театра с >= 300 мест");
    }
    
    @Test
    public void testGetTotalSeats() {
        Theater theater = new Theater("Театр", "Адрес", 100, 50, 30);
        
        Integer total = theater.getTotalSeats();
        
        assertEquals(total, Integer.valueOf(180));
    }
}
