package com.example.tradingview_technical_parser.controller;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.example.tradingview_technical_parser.service.ParsingService;
import com.example.tradingview_technical_parser.utils.PairnameMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/parser")
public class RestController {
    private final ParsingService parsingService;

    @Autowired
    public RestController(ParsingService parsingService) {
        this.parsingService = parsingService;
    }

    @GetMapping("/parse/matic")
    public CoinTechnicals.TechnicalsRecord parseMatic() throws InterruptedException {
        CoinTechnicals technicals = parsingService.parseTechnicals(new PairnameMetadata("https://www.tradingview.com/symbols/MATICUSD/technicals/"
                , "MATICUSD")
        );

        return technicals.getRecord();
    }

    @GetMapping("/parse/all")
    public CoinTechnicals.TechnicalsRecord[] parseAll() throws InterruptedException, ExecutionException, IOException {
        List<CoinTechnicals> technicals = parsingService.parseTechnicalsFromPairnamesFile();

        CoinTechnicals.TechnicalsRecord[] records = new CoinTechnicals.TechnicalsRecord[technicals.size()];

        for (int i = 0; i < technicals.size(); i++) {
            records[i] = technicals.get(i).getRecord();
        }

        return records;
    }

}
