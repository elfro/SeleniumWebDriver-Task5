package airbnb;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

public class ReserveApartment {

    private WebDriver driver;

    @BeforeMethod
    public void setUp() throws Exception {
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\test\\resources\\chromedriver.exe");
        driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        driver.manage().window().maximize();
        driver.get("https://airbnb.com/");
    }

    @AfterMethod
    public void tearDown() throws Exception {
     //   driver.quit();
    }

    @Test
    public void reserve() throws Exception {
        WebElement location  = driver.findElement(By.xpath(".//input[@class=\"LocationInput input-large\"]"));
        location.clear();
        location.sendKeys("Барселона Испания");
        WebElement dateFrom = driver.findElement(By.id("startDate"));
        dateFrom.clear();
        dateFrom.sendKeys("03.09.2016");
        WebElement dateTo = driver.findElement(By.id("endDate"));
        dateTo.clear();
        dateTo.sendKeys("05.09.2016");
        Select guests = new Select(driver.findElement(By.xpath(".//div[@id=\"searchbar\"]//select[@name=\"guests\"]")));
        guests.selectByVisibleText("2 гостя");
        driver.findElement(By.cssSelector(".btn.btn-primary.btn-large")).click();
        WebElement roomType = driver.findElement(By.xpath(".//div[@id='room-options']//input[@value=\"Entire home/apt\"]"));
        if(roomType.isSelected() == false)
            roomType.click();
        String apartment = ".//div[@id='search-results-panel']//img[contains(@alt, 'SUPER PENTHOUSE TERRACE CENTRAL')]";
        Wait fluenWait = new FluentWait(driver)
                .withTimeout(300, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);
        fluenWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(apartment)));
        String winHandleBefore = driver.getWindowHandle();
        driver.findElement(By.xpath(apartment)).click();
        for(String winHandle : driver.getWindowHandles()){
            driver.switchTo().window(winHandle);
        }
        String title = driver.getTitle();
        driver.close();
        driver.switchTo().window(winHandleBefore);

    }
}
