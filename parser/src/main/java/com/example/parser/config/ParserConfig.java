package com.example.parser.config;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

import java.net.URL;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableAsync
public class ParserConfig {

    @Value("${selenium.url}")
    private String seleniumUrl;

    @Bean
    public ExecutorService executor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public WebDriver webDriver() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("headless");

        try {
            return new RemoteWebDriver(
                    new URL(seleniumUrl),
                    options
            );
        } catch (Exception e) {

            WebDriver driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(Integer.MAX_VALUE));

            return driver;
        }
    }
}
