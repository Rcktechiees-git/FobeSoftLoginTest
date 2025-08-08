import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.Assert.*;

public class FobeSoftLoginTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void setUp() {
        // If ChromeDriver is not in PATH, set it explicitly:
        // System.setProperty("webdriver.chrome.driver", "/path/to/chromedriver");
        driver = new ChromeDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        driver.get("https://dev.fobesoft.com/#/login");
    }

    @After
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void testLoginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
        assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='username']")).isDisplayed());
        assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='password']")).isDisplayed());
        assertTrue(driver.findElement(By.xpath("//button[contains(text(), 'Log In')]"))..isDisplayed());
        assertTrue(driver.findElement(By.xpath("//input[@type='checkbox']")).isDisplayed());
        assertTrue(driver.findElement(By.linkText("Forgot Password?"))..isDisplayed());
        assertTrue(driver.findElement(By.linkText("Sign Up"))..isDisplayed());
    }

    @Test
    public void testInvalidLoginShowsError() {
        WebElement username = driver.findElement(By.xpath("//input[@formcontrolname='username']"));
        WebElement password = driver.findElement(By.xpath("//input[@formcontrolname='password']"));
        WebElement loginBtn = driver.findElement(By.xpath("//button[contains(text(), 'Log In')]"));

        username.clear();
        username.sendKeys("invalid_user");
        password.clear();
        password.sendKeys("wrong_password");
        loginBtn.click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
            By.xpath("//*[contains(text(),'invalid') or contains(text(),'Incorrect') or contains(@class,'error')]"
        ));
        assertNotNull(errorMsg);
    }

    @Test
    public void testForgotPasswordLink() {
        WebElement forgotLink = driver.findElement(By.linkText("Forgot Password?"));
        forgotLink.click();
        wait.until(ExpectedConditions.urlContains("forgot"));
        assertTrue(driver.getCurrentUrl().contains("forgot"));
    }

    @Test
    public void testSignUpLink() {
        WebElement signUpLink = driver.findElement(By.linkText("Sign Up"));
        signUpLink.click();
        wait.until(ExpectedConditions.urlContains("signup"));
        assertTrue(driver.getCurrentUrl().contains("signup"));
    }

    @Test
    public void testRememberMeCheckbox() {
        WebElement rememberMe = driver.findElement(By.xpath("//input[@type='checkbox']"));
        rememberMe.click();
        assertTrue(rememberMe.isSelected());
    }
}