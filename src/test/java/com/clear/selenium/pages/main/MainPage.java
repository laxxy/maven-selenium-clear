package com.clear.selenium.pages.main;

import com.clear.selenium.pages.BasePage;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

public class MainPage extends BasePage {
    private WebDriverWait wait;
    private Actions actions;
    private WebDriver driver;

    public MainPage() {
        this.driver = driver();
        actions = new Actions(driver());
        wait = new WebDriverWait(driver(), 10);
        PageFactory.initElements(driver(), this);
    }

    @FindBy(css = ".introjs-skipbutton")
    private WebElement skipTheTour;

    @Step
    public void doubleClickOn(String elementTitle) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format(".module-container .Module%s", elementTitle.replace(" ", "")))));
        wait.until(ExpectedConditions.elementToBeClickable(element));
        waitForElementNotMove(element);
        actions.doubleClick(element).build().perform();
        waitForNetwork(5);
    }


   /* @Step
    public Screenshot getBackgroundRowScreenshotWithoutElement(String title){
        final WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".header-row")));
        WebElement inner = driver.findElement(By.cssSelector(String.format(".module.Module%s", title)));
        setElementAttributeWithJS("style", "visibility: hidden", inner);
        waitForAnimation();
        Screenshot s = getElementScreenshot(element);
        TestListener.attachScreenshot(s);
        setElementAttributeWithJS("style", "visibility: true", inner);
        return s;
    }

    @Step
    public Screenshot getSoundCloudScreenshotWithoutAlbumCover() {

        final WebElement iframe = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".module.ModuleSoundCloud iframe")));
        driver.switchTo().frame(iframe);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".sc-background-orange.hidden")));
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".sc-background-orange.hidden")));
        driver.switchTo().defaultContent();
        Screenshot s = takeScreenshotWithoutAlbumCover(driver, iframe);
        TestListener.attachScreenshot(s);
        return s;
    }

    @Step
    public Screenshot getButtonScreenshot() {
        final WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".module.ModuleButton a")));
        Screenshot s = getElementScreenshot(element);
        TestListener.attachScreenshot(s);
        return s;
    }*/

    //Right click

    @Step
    public MainPage rightClickOn(String elementTitle) {
        WebElement source = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(String.format(".module-container .Module%s", elementTitle.replace(" ", "")))));
        rightClick(source);
        waitForAnimation();
        return this;
    }

    @Step
    public MainPage scrollDown() {
        actions.sendKeys(Keys.END).build().perform();
        return this;
    }

    @Step
    public MainPage scrollUp() {
        actions.sendKeys(Keys.HOME).build().perform();
        return this;
    }

   /* @Step
    public MainPage refreshPage() {
        super.refreshPage();
        waitForPageToLoad();
        waitForNetwork(15);
        return this;
    }*/

    protected void setElementAttributeWithJS(String attributeName, String attributeValue, WebElement webElement) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].setAttribute(arguments[1], arguments[2]);", webElement, attributeName,
                attributeValue);
    }

    @Step
    public MainPage hoverOnElement(String elementTitle, int elementIndex) {
        List<WebElement> elements = driver.findElements(By.cssSelector(String.format(".module-container.Module%s", elementTitle)));
        WebElement element = elements.get(elementIndex - 1);
        actions.moveToElement(element).build().perform();
        return this;
    }
}

