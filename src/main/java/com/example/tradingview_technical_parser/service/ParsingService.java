package com.example.tradingview_technical_parser.service;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.example.tradingview_technical_parser.coin.Decision;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class ParsingService {

    private static final String OSCILLATORS_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]";
    private static final String MOVING_AVERAGES_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[3]";
    private static final String SUMMARY_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[2]";

    /**
     * Retrieves the technicals for a coin using the provided WebDriver.
     *
     * @param driver the WebDriver instance for scraping coin technical data
     * @return CoinTechnicals object containing the technical analysis for the coin
     */
    private static CoinTechnicals parseCoinTechnicals(WebDriver driver) {
        pauseExecution();

        return new CoinTechnicals("MATICUSD",
                Decision.fromString(extractElementText(driver, OSCILLATORS_XPATH)),
                Decision.fromString(extractElementText(driver, MOVING_AVERAGES_XPATH)),
                Decision.fromString(extractElementText(driver, SUMMARY_XPATH))
        );
    }

    private static String extractElementText(WebDriver driver, String path) {
        return driver.findElement(By.xpath(path)).getText().split("\n")[6].toLowerCase();
    }

    /**
     * Pauses the execution of the current thread for 1000 milliseconds (1 second).
     * If the thread is interrupted while sleeping, the InterruptedException is ignored.
     * Instant parsing without pausing may return all values as NEUTRAL.
     */
    private static void pauseExecution() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    public CoinTechnicals parseTechnicals(String url) {
        WebDriver driver = getEdgeDriverWithDefaultOptions();
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(5));

        try {
            driver.get(url);

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));

            return parseCoinTechnicals(driver);

        } finally {
            driver.quit();
        }
    }

    private WebDriver getEdgeDriverWithDefaultOptions() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("headless");

        return new EdgeDriver(options);
    }
}
