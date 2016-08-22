package airbnb;

import org.testng.annotations.DataProvider;

public class DataProviderClass {
    @DataProvider(name = "apartmentData")
    public static Object[][] apartmentData() {
        return new Object[][]{
                { "Барселона Испания", "03.09.2016", "05.09.2016", "2 гостя", "SUPER PENTHOUSE TERRACE CENTRAL", true, "Дом/квартира целиком", 2, 0, 2 }
        };
    }
}
