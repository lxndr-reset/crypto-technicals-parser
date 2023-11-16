package com.example.tradingview_technical_parser.service;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.example.tradingview_technical_parser.coin.Decision;
import com.example.tradingview_technical_parser.utils.PairnameMetadata;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

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

    private static String extractElementText(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath)).getText().split("\n")[6].toLowerCase();
    }

    private static void waitFor(int duration) {
        try {
            Thread.sleep(Duration.ofSeconds(duration));
        } catch (InterruptedException ignored) {

        }
    }

    /**
     * Parses the technical analysis for a coin using the provided metadata.
     * Includes pausing.
     * Instant parsing without pausing may return all values as NEUTRAL.
     *
     * @param metadata the metadata for the coin, containing the pair name and URL
     * @return a CoinTechnicals object containing the technical analysis for the coin
     */
    public CoinTechnicals parseTechnicals(PairnameMetadata metadata) {
        WebDriver driver = getEdgeDriverWithDefaultOptions();

        waitFor(3);

        try {
            driver.get(metadata.getURL());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));

            return new CoinTechnicals(metadata.getPairName(),
                    Decision.fromString(extractElementText(driver, OSCILLATORS_XPATH)),
                    Decision.fromString(extractElementText(driver, MOVING_AVERAGES_XPATH)),
                    Decision.fromString(extractElementText(driver, SUMMARY_XPATH))
            );

        } finally {
            driver.quit();
        }
    }

    public CoinTechnicals parseTechnicals(PairnameMetadata metadata, int delayInSeconds) {
        WebDriver driver = getEdgeDriverWithDefaultOptions();

        waitFor(delayInSeconds);

        try {
            driver.get(metadata.getURL());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));

            return new CoinTechnicals(metadata.getPairName(),
                    Decision.fromString(extractElementText(driver, OSCILLATORS_XPATH)),
                    Decision.fromString(extractElementText(driver, MOVING_AVERAGES_XPATH)),
                    Decision.fromString(extractElementText(driver, SUMMARY_XPATH))
            );

        } finally {
            driver.quit();
        }
    }

    public List<CoinTechnicals> parseTechnicalsFromPairnamesFile() throws FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/tradingview_technical_parser/utils/pairnames"));
        Set<PairnameMetadata> pairnameMetadatas = new HashSet<>();

        String line;

        try {
            while ((line = reader.readLine()) != null) {
                pairnameMetadatas.add(new PairnameMetadata(line));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<CoinTechnicals> parsedTechnicals = new CopyOnWriteArrayList<>();

        pairnameMetadatas.parallelStream().forEach(metadata -> {
            CoinTechnicals technicals = this.parseTechnicals(metadata);

            if (technicals.areAllNeutral()) {
                technicals = this.parseTechnicals(metadata, 5);
            }
            parsedTechnicals.add(technicals);
        });

        return parsedTechnicals;
    }

    private WebDriver getEdgeDriverWithDefaultOptions() {
        EdgeOptions options = new EdgeOptions();
//        options.addArguments("headless");

        EdgeDriver edgeDriver = new EdgeDriver(options);
        edgeDriver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.MAX_VALUE));

        return edgeDriver;
    }
}
