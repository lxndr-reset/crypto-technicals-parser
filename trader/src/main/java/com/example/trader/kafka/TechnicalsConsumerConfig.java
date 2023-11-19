package com.example.trader.kafka;

import com.example.parser.technicals.CoinTechnicals;
import com.example.parser.technicals.serialization.TechnicalsRecordDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.support.converter.ByteArrayJsonMessageConverter;
import org.springframework.kafka.support.converter.RecordMessageConverter;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class TechnicalsConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, TechnicalsRecordDeserializer.class);

        return props;
    }

    @Bean
    public ConsumerFactory<String, CoinTechnicals.TechnicalsRecord> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(consumerConfig());
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, CoinTechnicals.TechnicalsRecord>>
    factory(ConsumerFactory<String, CoinTechnicals.TechnicalsRecord> consumerFactory, RecordMessageConverter recordMessageConverter) {

        return createKafkaListenerContainerFactory(consumerFactory, recordMessageConverter);
    }

    private ConcurrentKafkaListenerContainerFactory<String, CoinTechnicals.TechnicalsRecord> createKafkaListenerContainerFactory(
            ConsumerFactory<String, CoinTechnicals.TechnicalsRecord> consumerFactory, RecordMessageConverter recordMessageConverter) {

        ConcurrentKafkaListenerContainerFactory<String, CoinTechnicals.TechnicalsRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setRecordMessageConverter(recordMessageConverter);
        factory.setBatchListener(true);
        factory.setAutoStartup(true);

        return factory;
    }

    @Bean
    public RecordMessageConverter messageConverter() {
        return new ByteArrayJsonMessageConverter();
    }
}
