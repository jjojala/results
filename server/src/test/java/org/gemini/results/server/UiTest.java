/*
 * Copyright (C) 2015 Jari Ojala (jari.ojala@iki.fi)
 */
package org.gemini.results.server;

import java.io.IOException;
import org.glassfish.grizzly.http.server.HttpServer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

public class UiTest {
/*
    private static HttpServer server = null;

    @BeforeClass
    public static void startUp() throws IOException, InterruptedException {
        synchronized (UiTest.class) {
            server = ResultsServer.createServer(null);
            server.start();
            Thread.sleep(20000); // TODO: Fix this... slows build, is unreliable
        }
    }
    
    @AfterClass
    public static void tearDown() {
        synchronized (UiTest.class) {
            if (server != null)
                server.shutdownNow();
        }
    }

    @Test
    public void startWebDriver() {

        WebDriver driver = new FirefoxDriver();

        driver.navigate().to("http://localhost:8800/"); // TODO: configurable

        driver.findElement(By.cssSelector(".ng-touched")).sendKeys("2015-12-01T23:01");
        Assert.assertTrue("title should start differently",
                driver.getTitle().startsWith("Selenium Simplified"));

        driver.close();
        driver.quit();
    }
*/
}
