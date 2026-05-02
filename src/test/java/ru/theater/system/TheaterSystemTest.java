package ru.theater.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class TheaterSystemTest extends BaseSeleniumTest {

    @BeforeClass
    public void prepare() {
        resetDatabase();
    }

    // -------------------------------------------------------
    // Сценарий 1: Просмотр списка театров
    // -------------------------------------------------------

    @Test
    public void testTheaterListLoads() {
        driver.get(BASE_URL + "/theaters");

        String title = driver.getTitle();
        assertTrue(title.contains("Театральная касса"),
            "Заголовок страницы должен содержать 'Театральная касса'");

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Список театров не должен быть пустым");
    }

    @Test
    public void testTheaterListShowsNameAndAddress() {
        driver.get(BASE_URL + "/theaters");

        WebElement firstRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );
        String rowText = firstRow.getText();
        assertFalse(rowText.trim().isEmpty(),
            "Строка таблицы театров не должна быть пустой");
    }

    // -------------------------------------------------------
    // Сценарий 1: Поиск спектаклей
    // -------------------------------------------------------

    @Test
    public void testSearchByDirector() {
        driver.get(BASE_URL + "/theaters");

        Select directorSelect = new Select(
            driver.findElement(By.id("directorId"))
        );
        directorSelect.selectByIndex(1);

        driver.findElement(
            By.cssSelector("form.filters button[type='submit']")
        ).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Должен быть хотя бы один спектакль для выбранного режиссёра");

        String firstRowText = rows.get(0).getText();
        assertFalse(firstRowText.contains("не найдены"),
            "Не должно быть сообщения 'не найдены' для существующего режиссёра");
    }

    @Test
    public void testSearchByActor() {
        driver.get(BASE_URL + "/theaters");

        Select actorSelect = new Select(
            driver.findElement(By.id("actorId"))
        );
        actorSelect.selectByIndex(1);

        driver.findElement(
            By.cssSelector("form.filters button[type='submit']")
        ).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Должен быть хотя бы один спектакль для выбранного актёра");
    }

    @Test
    public void testSearchByDateWithResults() {
        driver.get(BASE_URL + "/theaters");

        driver.findElement(By.id("date")).sendKeys("2024-03-15");

        driver.findElement(
            By.cssSelector("form.filters button[type='submit']")
        ).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Должен быть хотя бы один спектакль на дату 2024-03-15");

        String firstRowText = rows.get(0).getText();
        assertFalse(firstRowText.contains("не найдены"),
            "Не должно быть сообщения 'не найдены' для существующей даты");
    }

    @Test
    public void testSearchByDateNoResults() {
        driver.get(BASE_URL + "/theaters");

        driver.findElement(By.id("date")).sendKeys("2000-01-01");

        driver.findElement(
            By.cssSelector("form.filters button[type='submit']")
        ).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertEquals(rows.size(), 1,
            "Должна быть одна строка с сообщением 'не найдены'");

        String rowText = rows.get(0).getText();
        assertTrue(rowText.contains("не найдены"),
            "Должно быть сообщение о том что спектакли не найдены");
    }

    // -------------------------------------------------------
    // Сценарий 3: Добавление театра
    // -------------------------------------------------------

    @Test
    public void testAddTheaterSuccess() {
        driver.get(BASE_URL + "/theaters/new");

        driver.findElement(By.id("name")).sendKeys("Тестовый театр");
        driver.findElement(By.id("address")).sendKeys("ул. Тестовая, 1");
        driver.findElement(By.id("seatsParterre")).clear();
        driver.findElement(By.id("seatsParterre")).sendKeys("100");
        driver.findElement(By.id("seatsBalcony")).clear();
        driver.findElement(By.id("seatsBalcony")).sendKeys("50");
        driver.findElement(By.id("seatsMezzanine")).clear();
        driver.findElement(By.id("seatsMezzanine")).sendKeys("30");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/theaters"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлен"),
            "Должно быть сообщение об успешном добавлении");

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Тестовый театр"),
            "Новый театр должен появиться в списке");
    }

    @Test
    public void testAddTheaterEmptyName() {
        driver.get(BASE_URL + "/theaters/new");

        driver.findElement(By.id("address")).sendKeys("ул. Тестовая, 1");
        driver.findElement(By.id("seatsParterre")).clear();
        driver.findElement(By.id("seatsParterre")).sendKeys("100");
        driver.findElement(By.id("seatsBalcony")).clear();
        driver.findElement(By.id("seatsBalcony")).sendKeys("50");
        driver.findElement(By.id("seatsMezzanine")).clear();
        driver.findElement(By.id("seatsMezzanine")).sendKeys("30");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/theaters/new")
            || driver.getCurrentUrl().contains("/theaters"),
            "При пустом названии не должны перейти на другую страницу");
    }

    @Test
    public void testAddTheaterEmptyAddress() {
        driver.get(BASE_URL + "/theaters/new");

        driver.findElement(By.id("name")).sendKeys("Театр без адреса");
        driver.findElement(By.id("seatsParterre")).clear();
        driver.findElement(By.id("seatsParterre")).sendKeys("100");
        driver.findElement(By.id("seatsBalcony")).clear();
        driver.findElement(By.id("seatsBalcony")).sendKeys("50");
        driver.findElement(By.id("seatsMezzanine")).clear();
        driver.findElement(By.id("seatsMezzanine")).sendKeys("30");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/theaters/new")
            || driver.getCurrentUrl().contains("/theaters"),
            "При пустом адресе не должны перейти на другую страницу");
    }

    // -------------------------------------------------------
    // Сценарий 3: Редактирование театра
    // -------------------------------------------------------

    @Test(dependsOnMethods = {"testTheaterListLoads"})
    public void testEditTheaterSuccess() {
        driver.get(BASE_URL + "/theaters");

        WebElement editBtn = driver.findElement(
            By.cssSelector("table tbody tr:first-child .btn-warning")
        );
        editBtn.click();

        wait.until(ExpectedConditions.urlContains("/theaters/edit"));

        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Обновлённый театр");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/theaters"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно обновлены"),
            "Должно быть сообщение об успешном обновлении");
    }

    // -------------------------------------------------------
    // Сценарий 3: Удаление театра
    // -------------------------------------------------------

    @Test
    public void testDeleteTheaterWithPlaysShowsError() {
        driver.get(BASE_URL + "/theaters");
        WebElement firstRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );
        WebElement deleteForm = firstRow.findElement(By.tagName("form"));

        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "arguments[0].onsubmit = function(){ return true; }; arguments[0].submit();",
            deleteForm
        );
        wait.until(ExpectedConditions.urlContains("/theaters"));

        WebElement error = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("error"))
        );

        assertTrue(
            error.getText().contains("Нельзя удалить"),
            "Должно быть сообщение о невозможности удалить театр со спектаклями. " +
            "Получено: " + error.getText()
        );
    }

    @Test
    public void testDeleteTheaterSuccess() {
        driver.get(BASE_URL + "/theaters/new");
        driver.findElement(By.id("name")).sendKeys("ТеатрДляУдаления123");
        driver.findElement(By.id("address")).sendKeys("ул. Временная, 99");
        driver.findElement(By.id("seatsParterre")).clear();
        driver.findElement(By.id("seatsParterre")).sendKeys("10");
        driver.findElement(By.id("seatsBalcony")).clear();
        driver.findElement(By.id("seatsBalcony")).sendKeys("10");
        driver.findElement(By.id("seatsMezzanine")).clear();
        driver.findElement(By.id("seatsMezzanine")).sendKeys("10");
        driver.findElement(By.cssSelector("button[type='submit']")).click();
        wait.until(ExpectedConditions.urlContains("/theaters"));

        String pageAfterAdd = driver.findElement(By.tagName("body")).getText();
        assertTrue(
            pageAfterAdd.contains("ТеатрДляУдаления123"),
            "Добавленный театр должен быть виден на странице"
        );

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        WebElement targetRow = null;
        for (WebElement row : rows) {
            if (row.getText().contains("ТеатрДляУдаления123")) {
                targetRow = row;
                break;
            }
        }
        assertNotNull(targetRow, "Добавленный театр должен быть в списке");

        WebElement deleteForm = targetRow.findElement(By.tagName("form"));
        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "arguments[0].onsubmit = function(){ return true; }; arguments[0].submit();",
            deleteForm
        );

        wait.until(ExpectedConditions.urlContains("/theaters"));

        String pageText = wait.until(driver2 -> {
            List<WebElement> msgs = driver2.findElements(By.className("message"));
            if (!msgs.isEmpty() && msgs.get(0).getText().contains("успешно удалён")) {
                return msgs.get(0).getText();
            }
            return null;
        });

        assertTrue(
            pageText.contains("успешно удалён"),
            "Должно быть сообщение об успешном удалении"
        );

        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertFalse(
            bodyText.contains("ТеатрДляУдаления123"),
            "Удалённый театр не должен быть в списке"
        );
    }
}