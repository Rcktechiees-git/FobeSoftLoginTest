import org.openqa.selenium.By;
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
                    "--window-size=1920,1080"
            );
        }

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(20));
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
        ((JavascriptExecutor)driver).executeScript("arguments[0].scrollIntoView(true);", element);
    }

    // Utility method to wait for any overlay/modal to disappear
    private void waitForOverlayToDisappear() {
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("forgotPasswordModel")));
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.className("modal-backdrop")));  // Optional: If there's a backdrop overlay
        } catch (TimeoutException ignored) {
            // In case the overlay never appears or disappears before the timeout
        }
    }


    @Test
    public void loginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class, 'banner-text') and contains(text(), 'Log In')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='EMail1' and @type='email']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@type='password']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[@id='login_btn']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='rememberMe1-input']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//u[contains(text(), 'Forgot Password?')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//u[contains(text(), 'Sign Up')]")).isDisplayed());
    }

    @Test
    public void invalidLoginShowsError() {
    // Wait for the username input field to be visible
    WebElement username = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input[@formcontrolname='username']")));
    
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
    
    // Click the login button
    loginBtn.click();
    
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
    WebElement forgotLink = wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//u[contains(text(), 'Forgot Password?')]")));
    
    // Scroll the link into view
    scrollIntoView(forgotLink);
    
    // Wait for any possible overlay to disappear before clicking
    waitForOverlayToDisappear();
    
    // Click the "Forgot Password?" link
    forgotLink.click();
    
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
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//u[contains(text(), 'Sign Up')]")));
    
    // Find the "Sign Up" link
    WebElement signUpLink = driver.findElement(By.xpath("//u[contains(text(), 'Sign Up')]"));
    
    // Scroll the link into view
    scrollIntoView(signUpLink);
    
    // Wait until the link is clickable
    wait.until(ExpectedConditions.elementToBeClickable(signUpLink)).click();
    
    // Wait for the URL to change to the "signup" page
    wait.until(ExpectedConditions.urlContains("signup"));
    
    // Assert that the current URL contains "signup"
    Assert.assertTrue(driver.getCurrentUrl().contains("signup"));
    
    // Navigate back to the login page
    driver.navigate().back();
    
    // Wait for the login page elements to be visible again (you can update this XPath if needed)
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
    
    // Click on the "Remember Me" checkbox
    rememberMe.click();
    
    // Assert that the checkbox is selected, checking the "checked" attribute or "aria-checked"
    Assert.assertTrue(rememberMe.isSelected() 
                      || "true".equals(rememberMe.getAttribute("aria-checked")) 
                      || rememberMe.getAttribute("checked") != null, 
                      "The 'Remember Me' checkbox is not selected.");
}
