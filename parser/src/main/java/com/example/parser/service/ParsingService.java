package com.example.parser.service;

import com.example.parser.technicals.CoinTechnicals;
import com.example.parser.technicals.Decision;
import com.example.parser.utils.PairnameMetadata;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.*;

import static java.util.stream.Collectors.toSet;
import static org.openqa.selenium.support.ui.ExpectedConditions.not;
import static org.openqa.selenium.support.ui.ExpectedConditions.textToBe;

@Service
@EnableAsync
public class ParsingService {
    private static final String OSCILLATORS_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]";
    private static final String MOVING_AVERAGES_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[3]";
    private static final String SUMMARY_XPATH = "//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[2]";
    private final ExecutorService executor;
    private final WebDriver webDriver;

    @Autowired
    public ParsingService(ExecutorService executor, WebDriver webDriver) {
        this.webDriver = webDriver;
        this.executor = executor;
    }

    /**
     * Retrieves the technicals for a coin using the provided WebDriver.
     *
     * @param driver the WebDriver instance for scraping coin technical data
     * @return CoinTechnicals object containing the technical analysis for the coin
     */


    private static String extractElementText(WebDriver driver, String xpath) {
        String text = driver.findElement(By.xpath(xpath)).getText();
        String[] split = text.split("\n");
        if (split.length > 6) return split[6].toLowerCase();
        return "";
    }

    private static Set<PairnameMetadata> fillMetadataFromFile(BufferedReader reader) {
        return reader.lines().map(PairnameMetadata::new).collect(toSet());
    }

    /**
     * When going to tradingview.com/technicals/etc, analysis results are loaded after body loading, so we
     * wait until their indexes aren't loaded.
     * <p>
     * Sometimes there are cases when parser returns old data, so added sleep()
     *
     * @param driver WebDriver with a parsed link
     * @throws InterruptedException
     */
    private static void waitUntilDataAreUpdated(WebDriver driver) throws InterruptedException {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(300));

        wait.until(ExpectedConditions.or(not(textToBe(By.xpath("//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]/div[2]/div[1]/span[2]"), "0")), not(textToBe(By.xpath("//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]/div[2]/div[2]/span[2]"), "0")), not(textToBe(By.xpath("//*[@id=\"js-category-content\"]/div[2]/div/section/div/div[4]/div[1]/div[2]/div[3]/span[2]"), "0"))));

        Thread.sleep(300);
    }

    /**
     * Parses the technical analysis for a coin using the provided metadata.
     * Includes pausing.
     *
     * @param metadata the metadata for the coin, containing the pair name and URL
     * @return a CoinTechnicals object containing the technical analysis for the coin
     */
    public CoinTechnicals parseTechnicals(PairnameMetadata metadata) {
        return getCoinTechnicals(metadata, webDriver);
    }

    private CoinTechnicals getCoinTechnicals(PairnameMetadata metadata, WebDriver driver) {
        try {
            driver.get(metadata.getURL());

            waitUntilDataAreUpdated(driver);

            return new CoinTechnicals(metadata.getPairName(), Decision.fromString(extractElementText(driver, OSCILLATORS_XPATH)), Decision.fromString(extractElementText(driver, MOVING_AVERAGES_XPATH)), Decision.fromString(extractElementText(driver, SUMMARY_XPATH)));

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            driver.quit();
        }
    }

    public List<CoinTechnicals> parseTechnicalsFromPairnamesFile() throws InterruptedException, FileNotFoundException {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("parser/src/main/resources/pairnames.txt"));
        } catch (FileNotFoundException e) { // We can try to find file in classpath.
            // It's possible when we run an app from .jar file
            reader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("pairnames.txt"))));
        }

        Set<PairnameMetadata> pairnameMetadata = fillMetadataFromFile(reader);
        List<Callable<CoinTechnicals>> runnableList = new ArrayList<>();

        for (PairnameMetadata metadata : pairnameMetadata) {
            runnableList.add(createCallableForMetadata(metadata));
        }

        List<Future<CoinTechnicals>> futures = executor.invokeAll(runnableList);
        List<CoinTechnicals> parsedTechnicals = new ArrayList<>();

        for (Future<CoinTechnicals> future : futures) {
            parsedTechnicals.add(CompletableFuture.supplyAsync(() -> {
                try {
                    return future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }).join());
        }

        return parsedTechnicals;
    }

    private Callable<CoinTechnicals> createCallableForMetadata(PairnameMetadata metadata) {
        return () -> {
            CoinTechnicals technicals = this.parseTechnicals(metadata);
            if (technicals.areAllNeutral()) {
                technicals = this.parseTechnicals(metadata);
            }
            return technicals;
        };
    }
}
