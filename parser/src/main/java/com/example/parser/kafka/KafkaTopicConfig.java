package com.example.parser.kafka;

import com.example.parser.technicals.CoinTechnicals;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTopicConfig {
    private KafkaTemplate<String, CoinTechnicals.TechnicalsRecord> kafkaTemplate;

    @Autowired
    public void setKafkaTemplate(KafkaTemplate<String, CoinTechnicals.TechnicalsRecord> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Bean
    public Map<String, String> coinsTopicConfig() {
        HashMap<String, String> config = new HashMap<>();
        config.put(TopicConfig.RETENTION_MS_CONFIG, "14400000");

        return config;
    }

    @Bean
    public NewTopic coinsTopic(Map<String, String> config) {
        NewTopic coinsTopic = new NewTopic("coins-topic", Runtime.getRuntime().availableProcessors(), ((short) 1));
        coinsTopic.configs(config);

        kafkaTemplate.setDefaultTopic(coinsTopic.name());

        return coinsTopic;
    }

}
