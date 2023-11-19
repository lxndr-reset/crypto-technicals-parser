package com.example.parser.utils;

import java.util.Objects;

public class PairnameMetadata {
    private String URL;
    private String pairName;

    public PairnameMetadata(String URL, String pairName) {
        this.URL = URL;
        this.pairName = pairName;
    }

    /**
     * Constructs a new PairnameMetadata object using the provided raw metadata.
     *
     * @param rawMetadata Example: https://www.tradingview.com/symbols/MATICUSD/technicals/::MATICUSD
     */
    public PairnameMetadata(String rawMetadata) {
        String[] metadataParts = rawMetadata.split("::");

        this.URL = metadataParts[0];
        this.pairName = metadataParts[1];
    }

    @Override
    public String toString() {
        return "PairnameMetadata{" +
                "URL='" + URL + '\'' +
                ", pairName='" + pairName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PairnameMetadata that)) return false;
        return Objects.equals(getURL(), that.getURL()) && Objects.equals(getPairName(), that.getPairName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getURL(), getPairName());
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getPairName() {
        return pairName;
    }

    public void setPairName(String pairName) {
        this.pairName = pairName;
    }
}
