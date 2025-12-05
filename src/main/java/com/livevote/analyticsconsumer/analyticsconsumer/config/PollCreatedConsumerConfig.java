package com.livevote.analyticsconsumer.analyticsconsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livevote.analyticsconsumer.analyticsconsumer.dto.PollDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PollCreatedConsumerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, PollDto> pollCreatedConsumerFactory(ObjectMapper mapper) {

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-service-consumer");

        // KEY DESERIALIZER WRAPPED
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, PollDto.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        JsonDeserializer<PollDto> jsonValueDeserializer = new JsonDeserializer<>(PollDto.class, mapper);
        ErrorHandlingDeserializer<PollDto> errorHandler = new ErrorHandlingDeserializer<>(jsonValueDeserializer);

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                errorHandler
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PollDto> pollCreatedKafkaListenerContainerFactory(ConsumerFactory<String, PollDto> consumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, PollDto> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
