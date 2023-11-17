package com.example.tradingview_technical_parser.service;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.example.tradingview_technical_parser.coin.Decision;
import com.example.tradingview_technical_parser.utils.PairnameMetadata;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

@Service
@EnableAsync
public class ParsingService {
    private static final String OSCILLATORS_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]";
    private static final String MOVING_AVERAGES_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[3]";
    private static final String SUMMARY_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[2]";
    private final ExecutorService executor;

    @Autowired
    public ParsingService(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Retrieves the technicals for a coin using the provided WebDriver.
     *
     * @param driver the WebDriver instance for scraping coin technical data
     * @return CoinTechnicals object containing the technical analysis for the coin
     */

    private static String extractElementText(WebDriver driver, String xpath) {
        return driver.findElement(By.xpath(xpath)).getText().split("\n")[6].toLowerCase();
    }


    private static Set<PairnameMetadata> readPairnameMetadata(BufferedReader reader) {
        Set<PairnameMetadata> pairnameMetadata = new HashSet<>();

        reader.lines().forEach(string -> {
            pairnameMetadata.add(new PairnameMetadata(string));
        });

        return pairnameMetadata;
    }

    /**
     * Parses the technical analysis for a coin using the provided metadata.
     * Includes pausing.
     *
     * @param metadata the metadata for the coin, containing the pair name and URL
     * @return a CoinTechnicals object containing the technical analysis for the coin
     */
    public CoinTechnicals parseTechnicals(PairnameMetadata metadata) {
        WebDriver driver = buildDriverWithOptions();

        return getCoinTechnicals(metadata, driver);
    }

    private CoinTechnicals getCoinTechnicals(PairnameMetadata metadata, WebDriver driver) {
        try {
            driver.get(metadata.getURL());

            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("body")));
            Thread.sleep(3000);

            return new CoinTechnicals(
                    metadata.getPairName(),
                    Decision.fromString(extractElementText(driver, OSCILLATORS_XPATH)),
                    Decision.fromString(extractElementText(driver, MOVING_AVERAGES_XPATH)),
                    Decision.fromString(extractElementText(driver, SUMMARY_XPATH))
            );

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    public List<CoinTechnicals> parseTechnicalsFromPairnamesFile() throws InterruptedException, ExecutionException, FileNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader("src/main/java/com/example/tradingview_technical_parser/utils/pairnames"));
        Set<PairnameMetadata> pairnameMetadata = readPairnameMetadata(reader);

        List<Callable<CoinTechnicals>> runnableList = new ArrayList<>();

        for (PairnameMetadata metadata : pairnameMetadata) {

            runnableList.add(() -> {
                CoinTechnicals technicals = this.parseTechnicals(metadata);

                if (technicals.areAllNeutral()) {
                    technicals = this.parseTechnicals(metadata);
                }

                return technicals;
            });
        }

        List<Future<CoinTechnicals>> futures = executor.invokeAll(runnableList);
        List<CoinTechnicals> parsedTechnicals = new ArrayList<>();

        for (Future<CoinTechnicals> future : futures) {
            while (!future.isDone()) {
                Thread.sleep(100);
            }

            parsedTechnicals.add(future.get());
        }

        return parsedTechnicals;
    }

    private WebDriver buildDriverWithOptions() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        WebDriver driver = new ChromeDriver(options);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.MAX_VALUE));

        return driver;
    }
}
