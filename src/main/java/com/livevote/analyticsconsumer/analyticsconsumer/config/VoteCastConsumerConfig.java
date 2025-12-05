package com.livevote.analyticsconsumer.analyticsconsumer.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.livevote.analyticsconsumer.analyticsconsumer.dto.PollDto;
import com.livevote.analyticsconsumer.analyticsconsumer.dto.VoteDto;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
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
public class VoteCastConsumerConfig {

    @Value("${kafka.bootstrap.servers}")
    private String bootstrapServers;

    @Bean
    public ConsumerFactory<String, VoteDto> voteCastConsumerFactory(ObjectMapper mapper) {

        Map<String, Object> config = new HashMap<>();

        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "analytics-service-consumer");

        // KEY DESERIALIZER WRAPPED
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        config.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);

        JsonDeserializer<VoteDto> jsonValueDeserializer = new JsonDeserializer<>(VoteDto.class, mapper);

        // 2. Wrap it in the ErrorHandlingDeserializer instance manually.
        ErrorHandlingDeserializer<VoteDto> errorHandler = new ErrorHandlingDeserializer<>(jsonValueDeserializer);

        // JSON TYPE
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE, VoteDto.class.getName());
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        return new DefaultKafkaConsumerFactory<>(
                config,
                new StringDeserializer(),
                errorHandler
        );
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, VoteDto>
    voteCastKafkaListenerContainerFactory(
            ConsumerFactory<String, VoteDto> consumerFactory) {

        ConcurrentKafkaListenerContainerFactory<String, VoteDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
