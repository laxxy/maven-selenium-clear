package com.clear.selenium.pages.main.button;

import com.clear.selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;

import java.util.List;

public class ButtonModule extends BasePage {
    WebDriver driver;
    Actions actions;
    WebDriverWait wait;

    public ButtonModule() {
        this.driver = driver();
        actions = new Actions(driver());
        wait = new WebDriverWait(driver(), 10);
        PageFactory.initElements(driver(), this);
    }

    @FindBy(css = ".module.ModuleButton")
    List<WebElement> buttons;

    @FindBy(css = ".button .btn")
    List<WebElement> button;

    @Step
    public String getButtonLinkInPreview(int... buttonIndex) {
        driver.switchTo().defaultContent();
        driver.switchTo().frame("preview-body");
        if (buttonIndex.length == 0)
            return button.get(0).getAttribute("href");
        else
            return button.get(buttonIndex[0] - 1).getAttribute("href");
    }

    @Step
    public String getButtonLink(int... buttonIndex) {
        if (buttonIndex.length == 0)
            return button.get(0).getAttribute("href");
        else
            return button.get(buttonIndex[0] - 1).getAttribute("href");
    }

    @Step
    public ButtonModule alignButtonToLeftBy(int elementNumber, boolean isFully) {
        WebElement element = buttons.get(elementNumber - 1);
        wait.until(ExpectedConditions.visibilityOf(element));
        try{
            actions.moveToElement(element).clickAndHold().build().perform();
            WebElement container = element.findElement(By.xpath(".."));
            actions.moveToElement(container).build().perform();
            actions.moveToElement(container, 100, container.getSize().getHeight() / 2).build().perform();
            if (isFully) {
                actions.moveToElement(container, 0, container.getSize().getHeight() / 2).build().perform();
            }
        } finally {
            actions.release().build().perform();
        }
        waitForNetwork(10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".align-left.ModuleButton")));
        return this;
    }

    @Step
    public ButtonModule alignButtonToRightBy(int elementNumber, boolean isFully) {
        WebElement element = buttons.get(elementNumber - 1);
        wait.until(ExpectedConditions.visibilityOf(element));
        try{
            actions.moveToElement(element).clickAndHold().build().perform();
            WebElement container = element.findElement(By.xpath(".."));
            actions.moveToElement(container, container.getSize().getWidth(), container.getSize().getHeight() / 2).build().perform();
            actions.moveToElement(container, container.getSize().getWidth() -200, container.getSize().getHeight() / 2).build().perform();

            String padding = element.findElement(By.xpath("..")).getCssValue("padding-right").replaceAll("[a-zA-Z:]+", "");
            if (isFully) {
                int i = 20;
                while (!padding.equals("0") && i > 0) {
                    actions.moveToElement(container, container.getSize().getWidth() - i, container.getSize().getHeight() / 2).build().perform();
                    waitForAnimation();
                    i -= 5;
                }
            }
        } finally {
            actions.release().build().perform();
        }
        waitForNetwork(10);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(".align-right.ModuleButton")));
        return this;
    }

    @Step
    public boolean isButtonAlignToRight() {
        try {
           return driver.findElement(By.cssSelector(".align-right.ModuleButton")).isDisplayed();
        } catch (Exception x) {
            return false;
        }
    }

    @Step
    public boolean isButtonAlignToRightInPreview() {
        try {
            return driver.findElement(By.xpath("//div[contains(@class,'align-right ModuleButton') and not(contains(@class,'ember-view'))]")).isDisplayed();
        } catch (Exception x) {
            return false;
        }
    }

    @Step
    public boolean isButtonAlignToLeft() {
        try {
            return driver.findElement(By.cssSelector(".align-left.ModuleButton")).isDisplayed();
        } catch (Exception x) {
            return false;
        }
    }

    @Step
    public boolean isButtonHasOnlyRightPadding(int elementNumber) {
        WebElement element = buttons.get(elementNumber - 1);
        String right = element.findElement(By.xpath("..")).getCssValue("padding-right").replaceAll("[a-zA-Z:]+", "");
        String left = element.findElement(By.xpath("..")).getCssValue("padding-left").replaceAll("[a-zA-Z:]+", "");
        return Integer.valueOf(left) == 0 && Integer.valueOf(right) > 0;
    }

    @Step
    public boolean isButtonHasOnlyLeftPadding(int elementNumber) {
        WebElement element = buttons.get(elementNumber - 1);
        String right = element.findElement(By.xpath("..")).getCssValue("padding-right").replaceAll("[a-zA-Z:]+", "");
        String left = element.findElement(By.xpath("..")).getCssValue("padding-left").replaceAll("[a-zA-Z:]+", "");
        return Integer.valueOf(left) > 0 && Integer.valueOf(right) == 0;
    }

    @Step
    public boolean isButtonAlignToLeftInPreview() {
        try {
            return driver.findElement(By.xpath("//div[contains(@class,'align-left ModuleButton') and not(contains(@class,'ember-view'))]")).isDisplayed();
        } catch (Exception x) {
            return false;
        }
    }
}
