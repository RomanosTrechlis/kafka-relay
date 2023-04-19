package io.github.romanostrechlis.kafkarelay.kafka;

import io.github.romanostrechlis.kafkarelay.transform.ITransformKafkaMessage;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class KafkaTopicListener {
    private static final Logger LOG = LoggerFactory.getLogger(KafkaTopicListener.class);

    private final ITransformKafkaMessage transformer;
    private final KafkaTemplate<String, String> kafkaClusterATemplate;
    private final KafkaTemplate<String, String> kafkaClusterBTemplate;

    @Value(value = "${kafka.clusterA.send.topic}")
    private String sendTopicA;

    @Value(value = "${kafka.clusterB.send.topic}")
    private String sendTopicB;

    public KafkaTopicListener(ITransformKafkaMessage transformer,
                              KafkaTemplate<String, String> kafkaClusterATemplate,
                              KafkaTemplate<String, String> kafkaClusterBTemplate) {
        this.transformer = transformer;
        this.kafkaClusterATemplate = kafkaClusterATemplate;
        this.kafkaClusterBTemplate = kafkaClusterBTemplate;
    }

    @KafkaListener(topics = "${kafka.clusterA.listen.topic}",
            groupId = "${kafka.clusterA.groupId}",
            containerFactory = "clusterAListenerFactory")
    public void listenClusterA(String message) {
        LOG.info("listenClusterA: {}", message);
        String clusterBMessage = transformer.transformToB(message);
        LOG.info("sending to cluster B: {}", clusterBMessage);

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(clusterBMessage)
                .setHeader(KafkaHeaders.TOPIC, sendTopicB)
                .build();
        kafkaClusterBTemplate.send(kafkaMessage);
    }

    @KafkaListener(topics = "${kafka.clusterB.listen.topic}",
            groupId = "${kafka.clusterB.groupId}",
            containerFactory = "clusterBListenerFactory")
    public void listenClusterB(String message) {
        LOG.info("listenClusterB: {}", message);
        String clusterAMessage = transformer.transformToA(message);
        LOG.info("sending to cluster A: {}", clusterAMessage);

        Message<String> kafkaMessage = MessageBuilder
                .withPayload(clusterAMessage)
                .setHeader(KafkaHeaders.TOPIC, sendTopicA)
                .build();
        kafkaClusterATemplate.send(kafkaMessage);
    }
}
