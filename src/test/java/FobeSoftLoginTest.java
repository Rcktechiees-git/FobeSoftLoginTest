// No package declaration (default package)

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.openqa.selenium.NoSuchElementException;
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
        wait = new WebDriverWait(driver, Duration.ofSeconds(30)); // Increased timeout
        driver.get("https://dev.fobesoft.com/#/login");
        // Wait for page to stabilize
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
    public void loginElementsPresent() {
        waitForOverlayToDisappear();
        // Updated XPath to handle case sensitivity and potential element changes
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='activeTab' and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]")));
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='username']"))).isDisplayed(), "Username field not displayed");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='Password1']"))).isDisplayed(), "Password field not displayed");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//button[@id='login_btn']"))).isDisplayed(), "Login button not displayed");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='rememberMe1-input']"))).isDisplayed(), "Remember Me checkbox not displayed");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//u[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'forgot password')]"))).isDisplayed(), "Forgot Password link not displayed");
        Assert.assertTrue(wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//u[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign up')]"))).isDisplayed(), "Sign Up link not displayed");
    }

    @Test
    public void invalidLoginShowsError() {
        waitForOverlayToDisappear();
        WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@formcontrolname='username']")));
        WebElement password = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@id='Password1']")));
        WebElement loginBtn = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//button[@id='login_btn']")));
        username.clear();
        username.sendKeys("invalid_user");
        password.clear();
        password.sendKeys("wrong_password");
        scrollIntoView(loginBtn);
        loginBtn.click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'invalid') " +
                        "or contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'incorrect') " +
                        "or contains(@class, 'error')]")));
        Assert.assertTrue(errorMsg.isDisplayed(), "Expected an error message after invalid login.");
    }

    @Test
    public void forgotPasswordLink() {
        waitForOverlayToDisappear();
        WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//u[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'forgot password')]")));
        scrollIntoView(forgotLink);
        try {
            forgotLink.click();
        } catch (Exception e) {
            // Fallback to JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", forgotLink);
        }
        try {
            wait.until(ExpectedConditions.urlContains("forgot"));
            Assert.assertTrue(driver.getCurrentUrl().contains("forgot"), "URL does not contain 'forgot'");
        } catch (TimeoutException e) {
            // Check if a modal appeared instead
            WebElement modal = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("forgotPasswordModel")));
            Assert.assertTrue(modal.isDisplayed(), "Forgot Password modal did not appear");
        }
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='activeTab' and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]")));
    }

    @Test
    public void signUpLink() {
        waitForOverlayToDisappear();
        WebElement signUpLink = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//u[contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'sign up')]")));
        scrollIntoView(signUpLink);
        try {
            signUpLink.click();
        } catch (Exception e) {
            // Fallback to JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", signUpLink);
        }
        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"), "URL does not contain 'signup'");
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//span[@class='activeTab' and contains(translate(text(), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), 'login')]")));
    }

    @Test
    public void rememberMeCheckbox() {
        waitForOverlayToDisappear();
        WebElement rememberMe = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//input[@id='rememberMe1-input']")));
        scrollIntoView(rememberMe);
        try {
            rememberMe.click();
        } catch (Exception e) {
            // Fallback to JavaScript click
            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", rememberMe);
        }
        // Check for checked state using multiple attributes
        boolean isChecked = rememberMe.isSelected() ||
                "true".equals(rememberMe.getAttribute("aria-checked")) ||
                rememberMe.getAttribute("checked") != null;
        Assert.assertTrue(isChecked, "Remember Me checkbox was not selected");
    }
}
