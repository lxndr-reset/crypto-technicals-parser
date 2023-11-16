package com.example.tradingview_technical_parser.coin;

public class CoinTechnicals {

    private String pairName;
    private Decision oscillators;
    private Decision movingAverages;
    private Decision summary;


    /**
     * Creates CoinTechnicals and uses the execution of the current thread for 1000 milliseconds (1 second).
     * TInterruptedException is ignored.
     * Instant parsing without pausing may return all values as NEUTRAL.
     */
    public CoinTechnicals(String pairName, Decision oscillators, Decision movingAverages, Decision summary) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        this.pairName = pairName;
        this.oscillators = oscillators;
        this.movingAverages = movingAverages;
        this.summary = summary;
    }

    public TechnicalsRecord getRecord() {

        return new CoinTechnicals.TechnicalsRecord(
                this.pairName,
                this.oscillators,
                this.movingAverages,
                this.summary
        );
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }

    public Decision getOscillators() {
        return oscillators;
    }

    public void setOscillators(Decision oscillators) {
        this.oscillators = oscillators;
    }

    public Decision getMovingAverages() {
        return movingAverages;
    }

    public void setMovingAverages(Decision movingAverages) {
        this.movingAverages = movingAverages;
    }

    public Decision getSummary() {
        return summary;
    }

    public void setSummary(Decision summary) {
        this.summary = summary;
    }

    public record TechnicalsRecord(String coinPair, Decision oscillators, Decision movingAverages, Decision summary) {
    }
}
