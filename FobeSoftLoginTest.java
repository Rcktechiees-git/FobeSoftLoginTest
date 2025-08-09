// No package line (default package)

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
        boolean headless = System.getenv("CI") != null
                || System.getenv("CODESPACES") != null
                || Boolean.getBoolean("headless");
        if (headless) {
            options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage", "--window-size=1920,1080");
        }
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        driver.get("https://dev.fobesoft.com/#/login");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @Test
    public void loginElementsPresent() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
        Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='username']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@formcontrolname='password']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//button[contains(text(), 'Log In')]")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.xpath("//input[@type='checkbox']")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.linkText("Forgot Password?")).isDisplayed());
        Assert.assertTrue(driver.findElement(By.linkText("Sign Up")).isDisplayed());
    }

    @Test
    public void invalidLoginShowsError() {
        WebElement username = driver.findElement(By.xpath("//input[@formcontrolname='username']"));
        WebElement password = driver.findElement(By.xpath("//input[@formcontrolname='password']"));
        WebElement loginBtn = driver.findElement(By.xpath("//button[contains(text(), 'Log In')]"));
        username.clear();
        username.sendKeys("invalid_user");
        password.clear();
        password.sendKeys("wrong_password");
        loginBtn.click();
        WebElement errorMsg = wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[contains(text(),'invalid') or contains(text(),'Incorrect') or contains(@class,'error')]")));
        Assert.assertNotNull(errorMsg);
    }

    @Test
    public void forgotPasswordLink() {
        WebElement forgotLink = driver.findElement(By.linkText("Forgot Password?"));
        forgotLink.click();
        wait.until(ExpectedConditions.urlContains("forgot"));
        Assert.assertTrue(driver.getCurrentUrl().contains("forgot"));
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
    }

    @Test
    public void signUpLink() {
        WebElement signUpLink = driver.findElement(By.linkText("Sign Up"));
        signUpLink.click();
        wait.until(ExpectedConditions.urlContains("signup"));
        Assert.assertTrue(driver.getCurrentUrl().contains("signup"));
        driver.navigate().back();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//h2[contains(text(), 'Log In')]")));
    }

    @Test
    public void rememberMeCheckbox() {
        WebElement rememberMe = driver.findElement(By.xpath("//input[@type='checkbox']"));
        rememberMe.click();
        Assert.assertTrue(rememberMe.isSelected());
    }
}
