package io.github.romanostrechlis.kafkarelay.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Profile("!test")
public class KafkaProducerConfig {

    @Value(value = "${kafka.clusterA.bootstrapAddress}")
    private String clusterABootstrapAddress;

    @Value(value = "${kafka.clusterB.bootstrapAddress}")
    private String clusterBBootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerClusterAFactory() {
        return new DefaultKafkaProducerFactory<>(
                producerFactory(clusterABootstrapAddress)
        );
    }

    @Bean
    public ProducerFactory<String, String> producerClusterBFactory() {
        return new DefaultKafkaProducerFactory<>(
                producerFactory(clusterBBootstrapAddress)
        );
    }

    @Bean
    public KafkaTemplate<String, String> kafkaClusterATemplate() {
        return new KafkaTemplate<>(producerClusterAFactory());
    }

    @Bean
    public KafkaTemplate<String, String> kafkaClusterBTemplate() {
        return new KafkaTemplate<>(producerClusterBFactory());
    }

    private Map<String, Object> producerFactory(String bootstrapAddress) {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        return props;
    }
}
