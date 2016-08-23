package com.clear.selenium.driver;

import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DriverFactory {

    private static List<WebDriverThread> webDriverThreadPool = Collections.synchronizedList(new ArrayList<>());
    private static ThreadLocal<WebDriverThread> driverThread;

    @BeforeClass
    public void instantiateDriverObject() {
        driverThread = new ThreadLocal<WebDriverThread>() {
            @Override
            protected WebDriverThread initialValue() {
                WebDriverThread webDriverThread = new WebDriverThread();
                webDriverThreadPool.add(webDriverThread);
                return webDriverThread;
            }
        };
    }

    public static WebDriver getDriver() {
        return driverThread.get().getDriver();
    }

    @AfterClass
    public void closeDriverObjects() throws Exception {
        webDriverThreadPool.forEach(WebDriverThread::quitDriver);
        for (WebDriverThread webDriverThread : webDriverThreadPool) {
            webDriverThread.quitDriver();
            driverThread.remove();
        }
        webDriverThreadPool.clear();
    }
}