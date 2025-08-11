import org.openqa.selenium.*;
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
        boolean headless = System.getenv("CI") != null
                || Boolean.getBoolean("headless");

        if (headless) {
            options.addArguments(
                    "--headless=new",
                    "--no-sandbox",
                    "--disable-dev-shm-usage",
                    "--window-size=1920,1080",
                    "--disable-gpu",
                    "--disable-web-security",
                    "--disable-features=VizDisplayCompositor"
            );
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        driver.get("https://dev.fobesoft.com/#/login");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    private void waitForOverlayToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("forgotPasswordModel")));
        } catch (TimeoutException ignored) {}

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));
        } catch (TimeoutException ignored) {}

        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".loading-overlay")));
        } catch (TimeoutException ignored) {}

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void safeClick(By locator) {
        waitForOverlayToDisappear();
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    @Test
    public void loginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));

        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='EMail1' and @type='email']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@type='password']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[@id='login_btn']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='rememberMe1-input']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//a[normalize-space()='Forgot Password?']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//a[@routerlink='/signup']")).isDisplayed());
    }

    @Test
    public void invalidLoginShowsError() {
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("EMail1")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("Password1")));

        username.clear();
        username.sendKeys("test@gmail.com");
        password.clear();
        password.sendKeys("test@123");

        safeClick(By.id("login_btn"));

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') " +
                        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect') " +
                        "or contains(@class,'error')]")));

        Assert.assertNotNull(errorMsg, "Expected an error message after invalid login.");
    }

   @Test
public void forgotPasswordLink() {
    System.out.println("Current URL before clicking: " + driver.getCurrentUrl());

    // Click the "Forgot Password?" link safely
    safeClick(By.xpath("//a[normalize-space()='Forgot Password?']"));

    // Wait for either URL change or presence of reset form
    try {
        wait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("forgot"),
                ExpectedConditions.presenceOfElementLocated(By.id("reset-password-form"))
        ));
    } catch (TimeoutException e) {
        System.err.println("Timeout waiting for forgot password page. Current URL: " + driver.getCurrentUrl());
        throw e;
    }

    // Assert URL contains "forgot"
    Assert.assertTrue(driver.getCurrentUrl().contains("forgot"),
            "Expected URL to contain 'forgot' but was: " + driver.getCurrentUrl());

    // Navigate back and verify login page is visible
    driver.navigate().back();
    wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));
}


    @Test
    public void signUpLink() {
        safeClick(By.xpath("//a[@routerlink='/signup']"));
        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"));

        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));
    }

    @Test
    public void rememberMeCheckbox() {
        By checkboxLocator = By.id("rememberMe1-input");
        safeClick(checkboxLocator);

        WebElement rememberMe = driver.findElement(checkboxLocator);
        Assert.assertTrue(rememberMe.isSelected()
                || "true".equals(rememberMe.getAttribute("aria-checked"))
                || rememberMe.getAttribute("checked") != null,
                "The 'Remember Me' checkbox is not selected.");
    }
}
