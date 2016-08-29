package airbnb;


import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.*;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

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
        driver.quit();
    }

    @Test(dataProvider = "apartmentData", dataProviderClass = DataProviderClass.class)
    public void reserve(
            String locationV,
            String dateFromV,
            String dateToV,
            String guestsV,
            String apartmentName,
            Boolean typeOfApartmentV,
            String expectedType,
            Integer expectedGuests,
            Integer expectedRooms,
            Integer expectedBeds
    ) throws Exception {
        WebElement location = driver.findElement(By.xpath(".//input[@class='LocationInput input-large']"));
        location.clear();
        location.sendKeys(locationV);
        WebElement dateFrom = driver.findElement(By.id("startDate"));
        dateFrom.clear();
        dateFrom.sendKeys(dateFromV);
        WebElement dateTo = driver.findElement(By.id("endDate"));
        dateTo.clear();
        dateTo.sendKeys(dateToV);
        Select guests = new Select(driver.findElement(By.xpath(".//div[@id='searchbar']//select[@name='guests']")));
        guests.selectByVisibleText(guestsV);
        driver.findElement(By.cssSelector(".btn.btn-primary.btn-large")).click();
        WebElement roomType = driver.findElement(By.xpath("//div[@data-name='room_types']//input[@type='checkbox' and @value='Entire home/apt']"));
        if (roomType.isSelected() != typeOfApartmentV) {
            roomType.click();
        }
        String apartment = ".//img[contains(@alt, '" + apartmentName + "')][1]";

        Wait fluenWait = new FluentWait(driver)
                .withTimeout(30, TimeUnit.SECONDS)
                .pollingEvery(2, TimeUnit.SECONDS)
                .ignoring(NoSuchElementException.class);
        fluenWait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(apartment)));
        String winHandleBefore = driver.getWindowHandle();
        driver.findElement(By.xpath(apartment)).click();
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
        String signInPopUp = ".//div[@class='panel-padding panel-body signup-login-form__extra-panel-body first']";
        (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOfElementLocated(By.xpath(signInPopUp)));
        WebElement signInElement = driver.findElement(By.xpath(signInPopUp));
        Actions builder = new Actions(driver);
        builder.moveToElement(signInElement, -40, 0).click().perform();
        Integer actualCountGuests = Integer.parseInt(driver.findElement(By.xpath(".//div[@class='col-sm-3' and contains(@data-reactid, '.0.1.1.0.$1')]")).getText().replaceAll("[а-яА-Я]| ", ""));
        Integer actualCountRooms = Integer.parseInt(driver.findElement(By.xpath(".//div[@class='col-sm-3' and contains(@data-reactid, '.0.1.1.0.$2')]")).getText().replaceAll("[а-яА-Я]| ", ""));
        String actualTypeOfApartment = driver.findElement(By.xpath(".//div[@class='col-sm-3' and contains(@data-reactid, '.0.1.1.0.$0')]")).getText();
        Integer actualCountBeds = Integer.parseInt(driver.findElement(By.xpath(".//div[@class='col-sm-3' and contains(@data-reactid, '.0.1.1.0.$3')]")).getText().replaceAll("[а-яА-Я]| ", ""));
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(actualTypeOfApartment, expectedType, "Type of apartment: failed");
        softAssert.assertEquals(actualCountGuests, expectedGuests, 3, "Guests count: failed ");
        softAssert.assertEquals(actualCountRooms, expectedRooms, 3, "Rooms count: failed");
        softAssert.assertEquals(actualCountBeds, expectedBeds, 3, "Beds room: failed");
        softAssert.assertAll();
        driver.close();
        driver.switchTo().window(winHandleBefore);
    }
}
