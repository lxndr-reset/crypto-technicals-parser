package com.example.tradingview_technical_parser.coin.serialization;

import com.example.tradingview_technical_parser.coin.CoinTechnicals;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;

public class TechnicalsRecordSerializer implements Serializer<CoinTechnicals.TechnicalsRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serialize(String topic, CoinTechnicals.TechnicalsRecord data) {
        try {
            return objectMapper.writeValueAsBytes(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
