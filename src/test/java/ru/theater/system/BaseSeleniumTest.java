package ru.theater.system;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.io.File;
import java.time.Duration;

public abstract class BaseSeleniumTest {

    protected WebDriver driver;
    protected WebDriverWait wait;

    protected static final String BASE_URL = "http://localhost:8080/theater";
    protected static final String GECKODRIVER_PATH = "/home/yury/geckodriver";
    protected static final String FIREFOX_BINARY_PATH = "/home/yury/firefox-esr/firefox";
    protected static final String GECKO_LOG_PATH = "/home/yury/Web_prac_6_sem/geckodriver.log";

    @BeforeClass(alwaysRun = true)
    public void setUp() {
        System.setProperty("webdriver.gecko.driver", GECKODRIVER_PATH);

        File tmpDir = new File("/home/yury/tmp");
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        System.setProperty("java.io.tmpdir", tmpDir.getAbsolutePath());

        FirefoxOptions options = new FirefoxOptions();
        options.setBinary(FIREFOX_BINARY_PATH);
        options.addArguments("-headless");
        // Важно для обхода некоторых ограничений песочницы Linux:
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");

        GeckoDriverService service = new GeckoDriverService.Builder()
                .usingDriverExecutable(new File(GECKODRIVER_PATH))
                .withLogFile(new File(GECKO_LOG_PATH))
                .build();

        driver = new FirefoxDriver(service, options);
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
            }
        }
    }

    protected void resetDatabase() {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "ant",
                    "-f",
                    "/home/yury/Web_prac_6_sem/database/build.xml",
                    "create-and-init"
            );
            pb.directory(new File("/home/yury/Web_prac_6_sem/database"));
            pb.inheritIO();
            Process p = pb.start();
            int exitCode = p.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Сброс БД завершился с кодом " + exitCode);
            }
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сбросить БД", e);
        }
    }
}