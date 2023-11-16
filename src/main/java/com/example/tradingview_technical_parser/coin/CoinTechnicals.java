package com.example.tradingview_technical_parser.coin;

public record CoinTechnicals(String coinPair, Decision oscillators, Decision movingAverages, Decision summary) {
}
