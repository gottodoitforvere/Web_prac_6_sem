package ru.theater.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class SessionSystemTest extends BaseSeleniumTest {

    @BeforeClass
    public void prepare() {
        resetDatabase();
    }

    // -------------------------------------------------------
    // Сценарий 2: Просмотр сеансов
    // -------------------------------------------------------

    @Test
    public void testSessionListLoads() {
        driver.get(BASE_URL + "/sessions?playId=1");

        wait.until(ExpectedConditions.urlContains("/sessions"));

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Сеансы спектакля"),
            "Заголовок должен содержать 'Сеансы спектакля'");

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Список сеансов не должен быть пустым");
    }

    @Test
    public void testSessionPageShowsPlayInfo() {
        driver.get(BASE_URL + "/sessions?playId=1");
        WebElement playInfo = driver.findElement(By.className("play-info"));
        String infoText = playInfo.getText();

        assertTrue(infoText.contains("Театр:"),
            "Должна быть информация о театре");
        assertTrue(infoText.contains("Режиссёр:"),
            "Должна быть информация о режиссёре");
        assertTrue(infoText.contains("Продолжительность:"),
            "Должна быть информация о продолжительности");
        assertTrue(infoText.contains("Цены:"),
            "Должна быть информация о ценах");
    }

    // -------------------------------------------------------
    // Сценарий 2: Покупка билетов — успех
    // -------------------------------------------------------

    @Test
    public void testBuyTicketsSuccess() {
        driver.get(BASE_URL + "/sessions?playId=1");

        WebElement firstRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );
        String beforeText = firstRow.getText();
        WebElement form = firstRow.findElement(
            By.cssSelector("form")
        );

        Select seatTypeSelect = new Select(
            form.findElement(By.name("seatType"))
        );
        seatTypeSelect.selectByValue("parterre");

        WebElement countInput = form.findElement(By.name("count"));
        countInput.clear();
        countInput.sendKeys("1");

        form.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sessions"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно выполнена"),
            "Должно быть сообщение об успешной покупке");
        WebElement updatedRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );
        String afterText = updatedRow.getText();
        assertNotEquals(beforeText, afterText,
            "Количество свободных мест должно измениться после покупки");
    }

    @Test
    public void testBuyTicketsNotEnoughSeats() {
        driver.get(BASE_URL + "/sessions?playId=1");

        WebElement firstRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );

        WebElement form = firstRow.findElement(By.cssSelector("form"));

        Select seatTypeSelect = new Select(
            form.findElement(By.name("seatType"))
        );
        seatTypeSelect.selectByValue("parterre");
        WebElement countInput = form.findElement(By.name("count"));
        countInput.clear();
        countInput.sendKeys("99999");

        form.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sessions"));

        WebElement error = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("error"))
        );
        assertTrue(error.getText().contains("Недостаточно"),
            "Должно быть сообщение о нехватке мест");
    }

    // -------------------------------------------------------
    // Сценарий 5: Управление сеансами — добавление
    // -------------------------------------------------------

    @Test
    public void testAddSessionSuccess() {
        driver.get(BASE_URL + "/sessions/new?playId=1");

        wait.until(ExpectedConditions.urlContains("/sessions/new"));

        driver.findElement(By.id("sessionDate")).sendKeys("2025-12-31");
        driver.findElement(By.id("sessionTime")).sendKeys("19:00");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sessions"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлен"),
            "Должно быть сообщение об успешном добавлении сеанса");

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("2025-12-31"),
            "Новый сеанс должен появиться в списке");
    }

    @Test
    public void testAddSessionAutoFillsSeats() {
        driver.get(BASE_URL + "/sessions/new?playId=1");

        driver.findElement(By.id("sessionDate")).sendKeys("2025-11-30");
        driver.findElement(By.id("sessionTime")).sendKeys("18:00");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sessions"));
        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("2025-11-30"),
            "Новый сеанс должен появиться в списке");

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );

        for (WebElement row : rows) {
            if (row.getText().contains("2025-11-30")) {
                String rowText = row.getText();
                assertFalse(rowText.contains(" 0 "),
                    "Свободные места не должны быть 0 после автозаполнения");
                break;
            }
        }
    }

    // -------------------------------------------------------
    // Сценарий 5: Удаление сеанса
    // -------------------------------------------------------

    @Test
    public void testDeleteSessionSuccess() {
        driver.get(BASE_URL + "/sessions/new?playId=1");
        driver.findElement(By.id("sessionDate")).sendKeys("2025-10-10");
        driver.findElement(By.id("sessionTime")).sendKeys("20:00");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/sessions"));
        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        WebElement targetRow = null;
        for (WebElement row : rows) {
            if (row.getText().contains("2025-10-10")) {
                targetRow = row;
                break;
            }
        }
        assertNotNull(targetRow, "Добавленный сеанс должен быть в списке");
        List<WebElement> forms = targetRow.findElements(By.tagName("form"));
        WebElement deleteForm = forms.get(forms.size() - 1);

        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "var f = arguments[0]; f.onsubmit = null; f.submit();",
            deleteForm
        );

        wait.until(ExpectedConditions.urlContains("/sessions"));

        String currentUrl = driver.getCurrentUrl();
        driver.get(currentUrl);

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertFalse(
            pageText.contains("2025-10-10"),
            "Удалённый сеанс не должен быть в списке"
        );
    }
}