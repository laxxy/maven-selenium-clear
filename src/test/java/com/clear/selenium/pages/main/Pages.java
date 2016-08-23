package com.clear.selenium.pages.main;

import com.clear.selenium.pages.BasePage;

/**
 * Created by Nikita Ovsyannikov on 13.07.2016.
 */
public final class Pages {
    private static ObjectsCollection<BasePage> pages = new ObjectsCollection<>();

    public static void clear() {
        pages.clear();
    }

    public static MainPage mainPage() {
        return pages.getInstance(MainPage.class);
    }
}
