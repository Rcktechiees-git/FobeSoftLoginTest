import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.Duration;

public class FobeSoftLoginTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeMethod
    public void setUp() {
        System.setProperty("webdriver.chrome.driver", "C:\\Users\\Suganya Nagavelu\\chromedriver-win64\\chromedriver-win64\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.get("https://dev.fobesoft.com/#/login");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterMethod
    public void tearDown() {
        driver.quit();
    }

    @Test
    public void testLoginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='username']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='password']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[contains(text(), 'Log In')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@type='checkbox']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.linkText("Forgot Password?")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.linkText("Sign Up")).isDisplayed());
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
            By.xpath("//*[contains(text(),'invalid') or contains(text(),'Incorrect') or contains(@class,'error')]")
        ));
        Assert.assertNotNull(errorMsg);
        Assert.assertTrue(errorMsg.isDisplayed());
    }

    @Test
    public void testForgotPasswordLink() {
        WebElement forgotLink = driver.findElement(By.linkText("Forgot Password?"));
        forgotLink.click();
        wait.until(ExpectedConditions.urlContains("forgot"));
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"));
    }

    @Test
    public void testSignUpLink() {
        WebElement signUpLink = driver.findElement(By.linkText("Sign Up"));
        signUpLink.click();
        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"));
    }

    @Test
    public void testRememberMeCheckbox() {
        WebElement rememberMe = driver.findElement(By.xpath("//input[@type='checkbox']"));
        rememberMe.click();
        Assert.assertTrue(rememberMe.isSelected());
    }
}
