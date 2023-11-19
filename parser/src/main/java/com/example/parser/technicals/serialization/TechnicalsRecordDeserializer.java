package com.example.parser.technicals.serialization;

import com.example.parser.technicals.CoinTechnicals;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;

public class TechnicalsRecordDeserializer implements Deserializer<CoinTechnicals.TechnicalsRecord> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public CoinTechnicals.TechnicalsRecord deserialize(String topic, byte[] data) {
        try {
            return objectMapper.readValue(data, CoinTechnicals.TechnicalsRecord.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
