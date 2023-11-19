package com.example.parser.kafka;

import com.example.parser.technicals.CoinTechnicals;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class KafkaMessageService {
    private final KafkaTemplate<String, CoinTechnicals.TechnicalsRecord> kafkaTemplate;

    public KafkaMessageService(KafkaTemplate<String, CoinTechnicals.TechnicalsRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Async
    public void sendNewTechnicalMessage(CoinTechnicals.TechnicalsRecord data) {
        kafkaTemplate.sendDefault(data);
    }

}
