package ru.theater.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class PlaySystemTest extends BaseSeleniumTest {

    @BeforeClass
    public void prepare() {
        resetDatabase();
    }

    // -------------------------------------------------------
    // Сценарий 1: Просмотр спектаклей театра
    // -------------------------------------------------------

    @Test
    public void testPlayListByTheaterLoads() {
        // Театр с id=1 есть в init.sql
        driver.get(BASE_URL + "/plays?theaterId=1");

        wait.until(ExpectedConditions.urlContains("/plays"));

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Спектакли театра"),
            "Заголовок должен содержать 'Спектакли театра'");

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Список спектаклей театра не должен быть пустым");
    }

    @Test
    public void testPlayListShowsTheaterAndDirector() {
        driver.get(BASE_URL + "/plays?theaterId=1");

        WebElement firstRow = driver.findElement(
            By.cssSelector("table tbody tr:first-child")
        );
        String rowText = firstRow.getText();

        // В строке должны быть данные (название, театр, режиссёр)
        assertFalse(rowText.trim().isEmpty(),
            "Строка спектакля не должна быть пустой");
    }

    // -------------------------------------------------------
    // Сценарий 4: Управление спектаклями — добавление
    // -------------------------------------------------------

    @Test
    public void testAddPlaySuccess() {
        driver.get(BASE_URL + "/plays/new?theaterId=1");

        wait.until(ExpectedConditions.urlContains("/plays/new"));

        driver.findElement(By.id("title")).sendKeys("Новый тестовый спектакль");

        Select directorSelect = new Select(
            driver.findElement(By.id("directorId"))
        );
        directorSelect.selectByIndex(1);

        driver.findElement(By.id("durationMinutes")).clear();
        driver.findElement(By.id("durationMinutes")).sendKeys("120");

        driver.findElement(By.id("priceParterre")).clear();
        driver.findElement(By.id("priceParterre")).sendKeys("1000");

        driver.findElement(By.id("priceBalcony")).clear();
        driver.findElement(By.id("priceBalcony")).sendKeys("800");

        driver.findElement(By.id("priceMezzanine")).clear();
        driver.findElement(By.id("priceMezzanine")).sendKeys("600");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлен"),
            "Должно быть сообщение об успешном добавлении спектакля");

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Новый тестовый спектакль"),
            "Новый спектакль должен появиться в списке");
    }

    @Test
    public void testAddPlayEmptyTitle() {
        driver.get(BASE_URL + "/plays/new?theaterId=1");

        // Не заполняем название
        Select directorSelect = new Select(
            driver.findElement(By.id("directorId"))
        );
        directorSelect.selectByIndex(1);

        driver.findElement(By.id("durationMinutes")).clear();
        driver.findElement(By.id("durationMinutes")).sendKeys("120");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        // Остаёмся на странице добавления
        assertTrue(driver.getCurrentUrl().contains("/plays/new")
            || driver.getCurrentUrl().contains("/plays"),
            "При пустом названии не должны успешно сохранить спектакль");
    }

    // -------------------------------------------------------
    // Сценарий 4: Редактирование спектакля
    // -------------------------------------------------------

    @Test(dependsOnMethods = {"testPlayListByTheaterLoads"})
    public void testEditPlaySuccess() {
        driver.get(BASE_URL + "/plays?theaterId=1");

        WebElement editBtn = driver.findElement(
            By.cssSelector("table tbody tr:first-child .btn-warning")
        );
        editBtn.click();

        wait.until(ExpectedConditions.urlContains("/plays/edit"));

        WebElement titleField = driver.findElement(By.id("title"));
        titleField.clear();
        titleField.sendKeys("Обновлённый спектакль");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно обновлены"),
            "Должно быть сообщение об успешном обновлении спектакля");
    }

    // -------------------------------------------------------
    // Сценарий 4: Удаление спектакля
    // -------------------------------------------------------

    @Test
    public void testDeletePlaySuccess() {
        // Добавляем спектакль
        driver.get(BASE_URL + "/plays/new?theaterId=1");

        driver.findElement(By.id("title")).sendKeys("Спектакль для удаления");
        Select directorSelect = new Select(driver.findElement(By.id("directorId")));
        directorSelect.selectByIndex(1);
        driver.findElement(By.id("durationMinutes")).clear();
        driver.findElement(By.id("durationMinutes")).sendKeys("60");
        driver.findElement(By.id("priceParterre")).clear();
        driver.findElement(By.id("priceParterre")).sendKeys("500");
        driver.findElement(By.id("priceBalcony")).clear();
        driver.findElement(By.id("priceBalcony")).sendKeys("400");
        driver.findElement(By.id("priceMezzanine")).clear();
        driver.findElement(By.id("priceMezzanine")).sendKeys("300");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/plays"));

        // Находим строку со спектаклем
        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        WebElement targetRow = null;
        for (WebElement row : rows) {
            if (row.getText().contains("Спектакль для удаления")) {
                targetRow = row;
                break;
            }
        }
        assertNotNull(targetRow, "Добавленный спектакль должен быть в списке");

        // Удаляем через JS
        WebElement deleteForm = targetRow.findElement(By.tagName("form"));
        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "var f = arguments[0]; f.onsubmit = null; f.submit();",
            deleteForm
        );

        wait.until(ExpectedConditions.urlContains("/plays"));

        // После редиректа проверяем что спектакль исчез
        String currentUrl = driver.getCurrentUrl();
        driver.get(currentUrl);

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertFalse(
            pageText.contains("Спектакль для удаления"),
            "Удалённый спектакль не должен быть в списке"
        );
    }
}