package com.clear.selenium.listeners;

import com.clear.selenium.pages.BaseTest;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.TestListenerAdapter;
import ru.yandex.qatools.allure.annotations.Attachment;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.comparison.ImageDiff;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TestListener extends TestListenerAdapter {

    private Set<String> jsErrorTestName = new HashSet<>();

    @Override
    public void onTestFailure(ITestResult result) {
        attachScreenshot();
        /*final List<JavaScriptError> jsErrors = JavaScriptError.readErrors(DriverFactory.getDriver());
        if (jsErrors.size() > 0) {
            jsErrorTestName.add(result.getTestName());
            JSErrors(jsErrors);
        }*/
    }

    /*@Override
    public void onTestSuccess(ITestResult tr) {
        final List<JavaScriptError> jsErrors = JavaScriptError.readErrors(DriverFactory.getDriver());
        if (jsErrors.size() > 0) {
            jsErrorTestName.add(tr.getName());
            JSErrors(jsErrors);
        }
    }*/

    @Override
    public void onFinish(ITestContext testContext) {
        String path = System.getProperty("user.dir") + "/target/allure-results/environment.xml";
        try {
            File file = new File(path);
            if (!file.exists()) {
                file.getParentFile().mkdirs();
                file.createNewFile();
            }
            List<String> collect = jsErrorTestName.stream().map(h -> {
                if (h != null && !h.isEmpty())
                    return h.replaceAll("[a-zA-Z]+", "");
                return "";
            }).collect(Collectors.toList());

            FileWriter writer = new FileWriter(file);
            writer.write("<qa:environment xmlns:qa=\"urn:model.commons.qatools.yandex.ru\">\n" +
                    "    <name>");
            writer.write("Number of JSErrors : " + collect.size());
            writer.write("( ");
            writer.write(collect.toString());
            writer.write(" )");
            writer.write("</name>");
            writer.write("</qa:environment>");
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Attachment(value = "Page screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        byte[] screenshotAs = null;
        try {
            screenshotAs = ((TakesScreenshot) BaseTest.getDriver()).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            fail(e);
        }

        return screenshotAs;
    }

    @Attachment(value = "Element screenshot", type = "image/png")
    public static byte[] attachScreenshot(Screenshot screenshot) {
        byte[] screenshotAs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot.getImage(), "png", baos);
            screenshotAs = baos.toByteArray();
        } catch (Exception ignored) {
        }
        return screenshotAs;
    }

    @Attachment(value = "Marked Image diff", type = "image/png")
    public static byte[] attachScreenshot(ImageDiff screenshot) {
        byte[] screenshotAs = null;
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(screenshot.getMarkedImage(), "png", baos);
            screenshotAs = baos.toByteArray();
        } catch (Exception ignored) {
        }
        return screenshotAs;
    }

    @Attachment(value = "Unable to save screenshot")
    private String fail(Exception e) {
        return String.format("%s\n%s", e.getMessage(), Arrays.toString(e.getStackTrace()));
    }

    /*@Attachment
    public String JSErrors(List<JavaScriptError> jsErrors) {
        return jsErrors.toString();
    }*/
}