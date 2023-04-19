package io.github.romanostrechlis.kafkarelay.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
@Profile("!test")
public class KafkaConsumerConfig {

    @Value(value = "${kafka.clusterA.bootstrapAddress}")
    private String clusterABootstrapAddress;

    @Value(value = "${kafka.clusterA.groupId}")
    private String clusterAGroupId;

    @Value(value = "${kafka.clusterB.bootstrapAddress}")
    private String clusterBBootstrapAddress;

    @Value(value = "${kafka.clusterB.groupId}")
    private String clusterBGroupId;

    @Bean
    public ConsumerFactory<String, String> consumerClusterAFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerFactory(clusterABootstrapAddress, clusterAGroupId)
        );
    }

    @Bean
    public ConsumerFactory<String, String> consumerClusterBFactory() {
        return new DefaultKafkaConsumerFactory<>(
                consumerFactory(clusterBBootstrapAddress, clusterBGroupId)
        );
    }

    private Map<String, Object> consumerFactory(String bootstrapAddress,
                                                String groupId) {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                groupId);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        return props;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    clusterAListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerClusterAFactory());
        return factory;
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String>
    clusterBListenerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerClusterBFactory());
        return factory;
    }
}
