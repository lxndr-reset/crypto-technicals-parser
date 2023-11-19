package org.example.trader.listener;

import com.example.parser.technicals.CoinTechnicals.TechnicalsRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class CoinTechnicalsListener {

    @KafkaListener(topics = {"coins-topic"}, groupId = "coins-topic")
    void coinsTopicListener(TechnicalsRecord record) {
        System.out.println(record);
    }
}
