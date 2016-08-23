package com.clear.selenium.pages;

import com.clear.selenium.driver.DriverFactory;
import com.clear.selenium.pages.main.Pages;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public class BaseTest extends DriverFactory {
    protected static WebDriver webDriver;
    private static WebDriverWait wait;
    private final String baseUrl = System.getProperty("base.url");
    private final String login = System.getProperty("login");
    private final String password = System.getProperty("password");

    protected String getLogin() {
        return login;
    }

    protected String getPassword() {
        return password;
    }

    @BeforeClass()
    public void setUpClass() {
        webDriver = getDriver();
        wait = new WebDriverWait(webDriver, 10);
        webDriver.manage().window().setSize(new Dimension(1500, 800));
    }

    @BeforeMethod
    public void setUp() throws Exception {
        String message = String.format("\n* Starting test  : %s",
                getClass().toString());
        System.out.println(message);
        webDriver.get(baseUrl);
        try {
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".uc-preloader")));
        } catch (Exception ignored) {
        }
    }

    @AfterClass
    public void tearDown() throws Exception {
        if (webDriver != null) {
            webDriver.quit();
        }
        Pages.clear();
    }
}