import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.*;

import java.time.Duration;

public class FobeSoftLoginTest {

    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();

        // Headless mode in CI environments
        boolean headless = System.getenv("CI") != null || Boolean.getBoolean("headless");
        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--window-size=1920,1080"
            );
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.get("https://app.fobesoft.com/#/login");
        wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath("//form")));
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center', inline: 'center'});", element);
    }

    private void waitForOverlayToDisappear() {
        try {
            WebElement modal = driver.findElement(By.id("forgotPasswordModel"));
            if (modal.isDisplayed()) {
                WebElement closeButton = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//div[@id='forgotPasswordModel']//button[contains(text(), 'Close') or contains(text(), 'Cancel')]")));
                closeButton.click();
                wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("forgotPasswordModel")));
            }
        } catch (NoSuchElementException | TimeoutException ignored) {
            // Modal not present or already closed
        }
    }

    @Test
    public void validateEmailFormatAndPassword() {
        waitForOverlayToDisappear();
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='username']")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='Password1']")));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@id='login_btn']")));

        // Test invalid email format
        username.clear();
        username.sendKeys("invalid_email");
        password.clear();
        password.sendKeys("testpassword");
        scrollIntoView(loginBtn);
        loginBtn.click();

        // Check for email format validation error
        try {
            WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'email') " +
                            "and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid')]")));
            Assert.assertTrue(errorMsg.isDisplayed(), "Expected an error message for invalid email format.");
        } catch (TimeoutException e) {
            Assert.fail("No error message displayed for invalid email format.");
        }

        // Test valid email format
        username.clear();
        username.sendKeys("test@example.com");
        password.clear();
        password.sendKeys("Test@1234");
        scrollIntoView(loginBtn);
        loginBtn.click();

        // Check if no email format error is displayed (assuming valid format doesn't trigger error)
        try {
            WebElement errorMsg = driver.findElement(
                    By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'email') " +
                            "and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid')]"));
            Assert.assertFalse(errorMsg.isDisplayed(), "Unexpected error message for valid email format.");
        } catch (NoSuchElementException | TimeoutException e) {
            // No error message is a good sign for valid email
        }

        // Verify password field accepts input
        Assert.assertEquals(password.getAttribute("value"), "Test@1234", "Password field did not accept the input correctly.");
    }
}
