package com.clear.selenium.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import ru.yandex.qatools.allure.annotations.Step;

public class SavePageBubble extends BasePage {
    private WebDriver driver;
    private WebDriverWait wait;
    private Actions actions;

    public SavePageBubble() {
        this.driver = driver();
        actions = new Actions(driver());
        wait = new WebDriverWait(driver(), 10);
        PageFactory.initElements(driver(), this);
    }

    @FindBy(css = "[name='pageTitle']")
    private WebElement pageTitleInput;

    @FindBy(css = "[name='pageTitle']+span")
    private WebElement pageTitleInputEror;

    @FindBy(css = ".save")
    private WebElement saveButton;

    @FindBy(xpath = "//div[@id='bubble' and .//div[contains(@class,'title') and contains(text(),'Save page')]]")
    private WebElement bubble;

    @FindBy(css = ".confirm-container")
    private WebElement pageSavedConfirmContainer;

    @FindBy(css = ".confirm-container .btn")
    private WebElement pageSavedConfirmContainerOkButton;

    @Step
    public SavePageBubble waitForSavePageDialog() {
        wait.until(ExpectedConditions.visibilityOf(bubble));
        wait.until(ExpectedConditions.elementToBeClickable(saveButton));
        return this;
    }

    @Step
    public SavePageBubble setPageTitle(String pageTitle) {
        pageTitleInput.clear();
        pageTitleInput.sendKeys(pageTitle);
        return this;
    }

    @Step
    public String getPageTitle() {
        return pageTitleInput.getAttribute("value");
    }

    @Step
    public void clickSaveButton() {
        driver.findElement(By.cssSelector(".title.current")).click();
        saveButton.click();
    }

    @Step
    public boolean isErrorDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageTitleInputEror));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Step
    public boolean isPageSavedConfirmContainerDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(pageSavedConfirmContainer));
            wait.until(ExpectedConditions.elementToBeClickable(pageSavedConfirmContainerOkButton));
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Step
    public void clickOkButton() {
        pageSavedConfirmContainerOkButton.click();
        wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".confirm-container")));
    }


}
