package com.example.tradingview_technical_parser.coin;

public enum Decision {
    NEUTRAL("neutral"),
    BUY("buy"),
    STRONG_BUY("strong buy"),
    SELL("sell"),
    STRONG_SELL("strong sell");
    private final String value;

    Decision(String value) {
        this.value = value;
    }

    public static Decision fromString(String text) {
        for (Decision decision : Decision.values()) {

            if (decision.value.equalsIgnoreCase(text)) {
                return decision;
            }
        }

        throw new IllegalArgumentException("No enum constant " + text);
    }

    public String getValue(){
        return this.value;
    }
}
