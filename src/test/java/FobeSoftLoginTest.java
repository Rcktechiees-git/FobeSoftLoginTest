// No package declaration (default package)

import org.openqa.selenium.By;
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

        // Headless mode in CI (GitHub Actions sets CI env variable)
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

        // Do NOT set webdriver.chrome.driver manually; Selenium Manager handles it.
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));

        driver.get("https://dev.fobesoft.com/#/login");
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    public void loginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='username']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='Password1']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[@id='login_btn']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@id='rememberMe1-input']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//u[contains(text(), 'Forgot Password?')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//u[contains(text(), 'Sign Up')]")).isDisplayed());
    }

    @Test
    public void invalidLoginShowsError() {
        WebElement username = driver.findElement(By.xpath("//input[@formcontrolname='username']"));
        WebElement password = driver.findElement(By.xpath("//input[@id='Password1']"));
        WebElement loginBtn = driver.findElement(By.xpath("//button[@id='login_btn']"));
        username.clear();
        username.sendKeys("invalid_user");
        password.clear();
        password.sendKeys("wrong_password");
        loginBtn.click();

        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'invalid') " +
                        "or contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'incorrect') " +
                        "or contains(@class,'error')]")));
        Assert.assertNotNull(errorMsg, "Expected an error message after invalid login.");
    }

    @Test
    public void forgotPasswordLink() {
        WebElement forgotLink = driver.findElement(By.xpath("//u[contains(text(), 'Forgot Password?')]"));
        forgotLink.click();
        wait.until(ExpectedConditions.urlContains("forgot"));
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"));
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
    }

    @Test
    public void signUpLink() {
        WebElement signUpLink = driver.findElement(By.xpath("//u[contains(text(), 'Sign Up')]"));
        signUpLink.click();
        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"));
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
    }

    @Test
    public void rememberMeCheckbox() {
        WebElement rememberMe = driver.findElement(By.xpath("//input[@id='rememberMe1-input']"));
        rememberMe.click();
        Assert.assertTrue(rememberMe.isSelected());
    }
}
