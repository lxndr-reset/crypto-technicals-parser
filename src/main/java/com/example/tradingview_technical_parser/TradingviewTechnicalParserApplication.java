package com.example.tradingview_technical_parser;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TradingviewTechnicalParserApplication {

    public static void main(String[] args) {
        System.setProperty("webdriver.chrome.driver","C:\\WebDriver\\bin\\chromedriver.exe");
        SpringApplication.run(TradingviewTechnicalParserApplication.class, args);
    }
}
