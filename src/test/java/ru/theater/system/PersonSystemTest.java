package ru.theater.system;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.*;

public class PersonSystemTest extends BaseSeleniumTest {

    @BeforeClass
    public void prepare() {
        resetDatabase();
    }

    // -------------------------------------------------------
    // Сценарий 6: Просмотр персон
    // -------------------------------------------------------

    @Test
    public void testPersonListLoads() {
        driver.get(BASE_URL + "/persons");

        wait.until(ExpectedConditions.urlContains("/persons"));

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Персоны"),
            "Заголовок страницы должен содержать 'Персоны'");

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        assertTrue(rows.size() > 0,
            "Список персон не должен быть пустым");
    }

    @Test
    public void testPersonListShowsRoles() {
        driver.get(BASE_URL + "/persons");

        String pageText = driver.findElement(By.tagName("body")).getText();

        assertTrue(pageText.contains("Режиссёр"),
            "Должна быть хотя бы одна персона с ролью Режиссёр");
        assertTrue(pageText.contains("Актёр"),
            "Должна быть хотя бы одна персона с ролью Актёр");
        assertTrue(pageText.contains("Режиссёр и актёр"),
            "Должна быть хотя бы одна персона с ролью Режиссёр и актёр");
    }

    // -------------------------------------------------------
    // Сценарий 6: Добавление персоны
    // -------------------------------------------------------

    @Test
    public void testAddPersonDirectorSuccess() {
        driver.get(BASE_URL + "/persons/new");

        driver.findElement(By.id("name")).sendKeys("Тестовый Режиссёр");

        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByValue("DIRECTOR");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлена"),
            "Должно быть сообщение об успешном добавлении персоны");

        String pageText = driver.findElement(By.tagName("body")).getText();
        assertTrue(pageText.contains("Тестовый Режиссёр"),
            "Новая персона должна появиться в списке");
    }

    @Test
    public void testAddPersonActorSuccess() {
        driver.get(BASE_URL + "/persons/new");

        driver.findElement(By.id("name")).sendKeys("Тестовый Актёр");

        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByValue("ACTOR");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлена"),
            "Должно быть сообщение об успешном добавлении актёра");
    }

    @Test
    public void testAddPersonBothSuccess() {
        driver.get(BASE_URL + "/persons/new");

        driver.findElement(By.id("name")).sendKeys("Тестовый Режиссёр-Актёр");

        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByValue("BOTH");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно добавлена"),
            "Должно быть сообщение об успешном добавлении");
    }

    @Test
    public void testAddPersonEmptyName() {
        driver.get(BASE_URL + "/persons/new");

        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByValue("ACTOR");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        assertTrue(driver.getCurrentUrl().contains("/persons/new")
            || driver.getCurrentUrl().contains("/persons"),
            "При пустом имени не должны перейти на другую страницу");
    }

    // -------------------------------------------------------
    // Сценарий 6: Редактирование персоны
    // -------------------------------------------------------

    @Test(dependsOnMethods = {"testPersonListLoads"})
    public void testEditPersonSuccess() {
        driver.get(BASE_URL + "/persons");

        WebElement editBtn = driver.findElement(
            By.cssSelector("table tbody tr:first-child .btn-warning")
        );
        editBtn.click();

        wait.until(ExpectedConditions.urlContains("/persons/edit"));

        WebElement nameField = driver.findElement(By.id("name"));
        nameField.clear();
        nameField.sendKeys("Обновлённая Персона");

        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        assertTrue(message.getText().contains("успешно обновлены"),
            "Должно быть сообщение об успешном обновлении персоны");
    }

    // -------------------------------------------------------
    // Сценарий 6: Удаление персоны
    // -------------------------------------------------------

    @Test
    public void testDeletePersonInUseShowsError() {
        driver.get(BASE_URL + "/persons");

        WebElement deleteForm = driver.findElement(
            By.cssSelector("table tbody tr:first-child form")
        );

        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;
        js.executeScript(
            "arguments[0].onsubmit = function(){ return true; }; arguments[0].submit();",
            deleteForm
        );

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement error = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("error"))
        );
        assertTrue(error.getText().contains("Нельзя удалить"),
            "Должно быть сообщение о невозможности удалить используемую персону");
    }

    @Test
    public void testDeletePersonSuccess() {
        driver.get(BASE_URL + "/persons/new");
        driver.findElement(By.id("name")).sendKeys("УникальнаяПерсонаДляУдаления");
        Select roleSelect = new Select(driver.findElement(By.id("role")));
        roleSelect.selectByValue("ACTOR");
        driver.findElement(By.cssSelector("button[type='submit']")).click();

        wait.until(ExpectedConditions.urlContains("/persons"));

        String pageAfterAdd = driver.findElement(By.tagName("body")).getText();
        assertTrue(
            pageAfterAdd.contains("УникальнаяПерсонаДляУдаления"),
            "Добавленная персона должна быть видна на странице"
        );

        List<WebElement> rows = driver.findElements(
            By.cssSelector("table tbody tr")
        );
        WebElement targetRow = null;
        for (WebElement row : rows) {
            if (row.getText().contains("УникальнаяПерсонаДляУдаления")) {
                targetRow = row;
                break;
            }
        }
        assertNotNull(targetRow, "Добавленная персона должна быть в списке");

        WebElement editBtn = targetRow.findElement(By.cssSelector(".btn-warning"));
        String editHref = editBtn.getAttribute("href");
        String personId = editHref.substring(editHref.lastIndexOf("=") + 1);

        driver.get(BASE_URL + "/persons");
        wait.until(ExpectedConditions.urlContains("/persons"));

        wait.until(driver2 ->
            driver2.findElements(By.className("message")).isEmpty()
        );

        org.openqa.selenium.JavascriptExecutor js =
            (org.openqa.selenium.JavascriptExecutor) driver;

        js.executeScript(
            "var form = document.createElement('form');" +
            "form.method = 'POST';" +
            "form.action = '" + BASE_URL + "/persons/delete';" +
            "var input = document.createElement('input');" +
            "input.type = 'hidden';" +
            "input.name = 'id';" +
            "input.value = '" + personId + "';" +
            "form.appendChild(input);" +
            "document.body.appendChild(form);" +
            "form.submit();"
        );

        wait.until(ExpectedConditions.urlContains("/persons"));

        WebElement message = wait.until(
            ExpectedConditions.presenceOfElementLocated(By.className("message"))
        );
        String messageText = message.getText();

        assertTrue(
            messageText.contains("успешно удалена"),
            "Должно быть сообщение об успешном удалении. Получено: " + messageText
        );

        String bodyText = driver.findElement(By.tagName("body")).getText();
        assertFalse(
            bodyText.contains("УникальнаяПерсонаДляУдаления"),
            "Удалённая персона не должна быть в списке"
        );
    }
}