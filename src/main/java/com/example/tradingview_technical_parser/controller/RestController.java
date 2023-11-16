package com.example.tradingview_technical_parser.controller;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.example.tradingview_technical_parser.service.ParsingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/parser")
public class RestController {
    private final ParsingService parsingService;

    @Autowired
    public RestController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @GetMapping("/parse/matic")
    public CoinTechnicals parseMatic() {
        CoinTechnicals technicals = parsingService.parseTechnicals("https://www.tradingview.com/symbols/MATICUSD/technicals/");

        return technicals;
    }

}
