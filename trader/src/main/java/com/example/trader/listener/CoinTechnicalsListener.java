package com.example.trader.listener;

import com.example.parser.technicals.CoinTechnicals;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CoinTechnicalsListener {

    @KafkaListener(topics = {"coins-topic"}, groupId = "coins-topic")
    void coinsTopicListener(CoinTechnicals.TechnicalsRecord record) {
        System.out.println(record);
    }
}
