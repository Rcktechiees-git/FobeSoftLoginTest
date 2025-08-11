import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
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

    // Utility method to scroll an element into view
    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Utility method to wait for any overlay/modal to disappear
    private void waitForOverlayToDisappear() {
        try {
            // Wait for the modal to be invisible if it exists
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("forgotPasswordModel")));
        } catch (TimeoutException ignored) {
            // Modal might not exist, which is fine
        }
        
        try {
            // Also check for backdrop overlay
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));
        } catch (TimeoutException ignored) {
            // Backdrop might not exist, which is fine
        }
        
        // Give a small pause to ensure any transitions are complete
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Utility method to click element with fallback to JavaScript click
    private void safeClick(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException e) {
            // If regular click fails, use JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        }
    }

    @Test
    public void loginElementsPresent() {
        // Wait for the banner text with 'Log In' to be visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));

        // Check if the login elements are displayed
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='EMail1' and @type='email']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@type='password']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[@id='login_btn']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='rememberMe1-input']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//a[normalize-space()='Forgot Password?']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//a[@routerlink='/signup']")).isDisplayed());
    }

    @Test
    public void invalidLoginShowsError() {
        // Wait for the username input field to be visible
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='EMail1' and @type='email']")));

        // Wait for the password input field to be visible
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='Password1']")));

        // Wait for the login button to be clickable
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@id='login_btn']")));

        // Clear any pre-filled text and enter invalid username and password
        username.clear();
        username.sendKeys("test@gmail.com");
        password.clear();
        password.sendKeys("test@123");

        // Wait for any overlay or modal to disappear before clicking the login button
        waitForOverlayToDisappear();

        // Click the login button using safe click method
        safeClick(loginBtn);

        // Wait for the error message to appear (adjusting to ensure it waits for visibility)
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') " +
                    "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect') " +
                    "or contains(@class,'error')]")));

        // Assert that the error message is visible
        Assert.assertNotNull(errorMsg, "Expected an error message after invalid login.");
    }

    @Test
    public void forgotPasswordLink() {
        // Wait until the "Forgot Password?" link is visible
        WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//a[normalize-space()='Forgot Password?']")));

        // Scroll the link into view
        scrollIntoView(forgotLink);

        // Wait for any possible overlay to disappear before clicking
        waitForOverlayToDisappear();

        // Click the "Forgot Password?" link using safe click method
        safeClick(forgotLink);

        // Wait until the URL changes to include "forgot"
        wait.until(ExpectedConditions.urlContains("forgot"));

        // Assert that the URL contains the word "forgot"
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"));

        // Navigate back to the login page
        driver.navigate().back();

        // Wait for the login page elements to be visible again
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));
    }

    @Test
    public void signUpLink() {
        // Wait until the "Sign Up" link is visible
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[@routerlink='/signup']")));

        // Find the "Sign Up" link
        WebElement signUpLink = driver.findElement(By.xpath("//a[@routerlink='/signup']"));

        // Scroll the link into view
        scrollIntoView(signUpLink);

        // Wait until the link is clickable and click using safe click method
        wait.until(ExpectedConditions.elementToBeClickable(signUpLink));
        safeClick(signUpLink);

        // Wait for the URL to change to the "signup" page
        wait.until(ExpectedConditions.urlContains("signup"));

        // Assert that the current URL contains "signup"
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"));

        // Navigate back to the login page
        driver.navigate().back();

        // Wait for the login page elements to be visible again
        wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));
    }

    @Test
    public void rememberMeCheckbox() {
        // Wait for any overlays or modals to disappear
        waitForOverlayToDisappear();

        // Locate the "Remember Me" checkbox
        WebElement rememberMe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@id='rememberMe1-input']")));

        // Scroll the checkbox into view to make sure it is interactable
        scrollIntoView(rememberMe);

        // Wait for the checkbox to be clickable
        wait.until(ExpectedConditions.elementToBeClickable(rememberMe));

        // Click on the "Remember Me" checkbox using safe click method
        safeClick(rememberMe);

        // Assert that the checkbox is selected, checking the "checked" attribute or "aria-checked"
        Assert.assertTrue(rememberMe.isSelected() 
                          || "true".equals(rememberMe.getAttribute("aria-checked")) 
                          || rememberMe.getAttribute("checked") != null, 
                          "The 'Remember Me' checkbox is not selected.");
    }
}
