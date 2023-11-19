package com.example.tradingview_technical_parser.kafka;

import com.example.tradingview_technical_parser.technicals.CoinTechnicals;
import com.example.tradingview_technical_parser.technicals.serialization.TechnicalsRecordSerializer;
import org.apache.kafka.clients.producer.RoundRobinPartitioner;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

import static org.apache.kafka.clients.producer.ProducerConfig.*;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> producerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(PARTITIONER_CLASS_CONFIG, RoundRobinPartitioner.class);
        props.put(BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(VALUE_SERIALIZER_CLASS_CONFIG, TechnicalsRecordSerializer.class);

        return props;
    }

    @Bean
    public ProducerFactory<String, CoinTechnicals.TechnicalsRecord> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfig());
    }

    @Bean
    public KafkaTemplate<String, CoinTechnicals.TechnicalsRecord> kafkaTemplate(ProducerFactory<String, CoinTechnicals.TechnicalsRecord> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

}
