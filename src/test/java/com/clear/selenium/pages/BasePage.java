package com.clear.selenium.pages;

import com.clear.selenium.listeners.TestListener;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class BasePage {
    private static WebDriver driver;
    private Actions actions;
    private WebDriverWait wait;

    public BasePage() {
        driver = BaseTest.webDriver;
        actions = new Actions(driver());
        wait = new WebDriverWait(driver(), 10);
    }

    protected void wait(int millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected static WebDriver driver() {
        return driver;
    }

    protected void setElementAttributeWithJS(String attributeName, String attributeValue, WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", webElement, attributeName,
                attributeValue);
    }

    protected void clickWithJS(WebElement element) {
        final String javaScript = "if(document.createEvent){" +
                "var evObj = document.createEvent('MouseEvents');" +
                "evObj.initEvent('click', true, false);" + "" +
                "arguments[0].dispatchEvent(evObj);" +
                "} else if(document.createEventObject){" +
                "arguments[0].fireEvent('onclick');" +
                "}";
        ((JavascriptExecutor) driver).executeScript(javaScript, element);
    }

    protected void rightClick(WebElement element) {
        try {
            actions.moveToElement(element, element.getSize().getWidth() / 3, element.getSize().getHeight() >> 1).contextClick().build().perform();

            System.out.println("Sucessfully Right clicked on the element");
        } catch (StaleElementReferenceException e) {
            System.out.println("Element is not attached to the page document "
                    + Arrays.toString(e.getStackTrace()));
        } catch (NoSuchElementException e) {
            System.out.println("Element " + element + " was not found in DOM "
                    + Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            System.out.println("Element " + element + " was not clickable "
                    + Arrays.toString(e.getStackTrace()));
        }
    }

    public static void waitForNetwork(int timeoutInSeconds) {
        System.out.println("Checking active ajax calls by calling jquery.active");
        try {
            if (driver instanceof JavascriptExecutor) {
                JavascriptExecutor jsDriver = (JavascriptExecutor) driver;
                for (int i = 0; i < timeoutInSeconds; i++) {
                    try {
                        Object numberOfAjaxConnections = jsDriver.executeScript("return jQuery.active");
                        if (numberOfAjaxConnections instanceof Long) {
                            Long n = (Long) numberOfAjaxConnections;
                            System.out.println("Number of active jquery ajax calls: " + n);
                            if (n == 0L)
                                break;
                        }
                        Thread.sleep(1000);
                    } catch (Exception ignored) {
                    }
                }
            } else {
                System.out.println("Web driver: " + driver + " cannot execute javascript");
            }
        } catch (Exception ignored) {
        }
    }

    protected void waitForPageLoad() {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait.until((ExpectedCondition<Boolean>) wdriver -> ((JavascriptExecutor) driver).executeScript(
                "return document.readyState"
        ).equals("complete"));
    }

    /*protected Screenshot takeScreenshotWithoutAlbumCover(WebDriver driver, WebElement element) {
        ImageCropper cropper = new DefaultCropper();
        ShootingStrategy shootingStrategy = new SimpleShootingStrategy();
        int x = element.getLocation().getX();
        int y = element.getLocation().getY();
        int h = element.getSize().getHeight();
        int w = element.getSize().getWidth();
        driver.switchTo().frame(element);
        int width = driver.findElement(By.cssSelector(".sound__artwork")).getSize().getWidth() + 2;
        Set<Coords> elementCoords = new HashSet<>();
        elementCoords.add(new Coords(x + width, y, w - width, h));
        BufferedImage shot = shootingStrategy.getScreenshot(driver, elementCoords);
        Screenshot screenshot = cropper.crop(shot, shootingStrategy.prepareCoords(elementCoords));
        driver.switchTo().defaultContent();
        return screenshot;
    }*/

    public void waitForAnimation() {
        WebDriverWait wait = new WebDriverWait(driver, 30);
        wait(3000);
        wait.until((ExpectedCondition<Boolean>) wdriver -> ((JavascriptExecutor) driver).executeScript(
                "return document.readyState"
        ).equals("complete"));
    }

    @Step
    public int getWindowHeight() {
        return Integer.parseInt(((JavascriptExecutor) driver).executeScript("return $( window ).height();").toString());
    }

    protected WebElement scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);",
                element);
        return element;
    }

    protected void scroll(int y) {
        String script = String.format("scroll(0, %d);", y);
        ((JavascriptExecutor) driver).executeScript(script);
    }

    protected Screenshot getElementScreenshot(WebElement element) {
        return new AShot().takeScreenshot(driver, element);
    }

    protected WebElement waitForElementNotMove(final WebElement element) {
        wait.until((ExpectedCondition<Boolean>) wdriver -> {
            Point loc = element.getLocation();
            wait(500);
            return element.getLocation().equals(loc);
        });
        return element;
    }
    protected void waitForElementsChanged(final String expected, final WebElement element, String cssValue) {
        wait.until((ExpectedCondition<Boolean>) wdriver -> {
            wait(2000);
            return element.getCssValue(cssValue).equals(String.valueOf(expected));
        });
    }

    @Step
    public ImageDiff getDiff(Screenshot start, Screenshot end) {
        ImageDiff diff = new ImageDiffer().makeDiff(start, end);
        TestListener.attachScreenshot(diff);
        return diff;
    }

    @Step
    public ImageDiff getDiff(BufferedImage start, BufferedImage end) {
        ImageDiff diff = new ImageDiffer().makeDiff(start, end);
        TestListener.attachScreenshot(diff);
        return diff;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    protected void uploadImage(WebElement fileInput, String filePath) {
        setElementAttributeWithJS("style", "display:block", fileInput.findElement(By.xpath("..")));
        fileInput.sendKeys(filePath);
        setElementAttributeWithJS("style", "display:none", fileInput.findElement(By.xpath("..")));
        waitForNetwork(10);
    }

    protected BasePage refreshPage() {
        driver.navigate().to(driver.getCurrentUrl());
        waitForNetwork(5);
        return this;
    }
}
