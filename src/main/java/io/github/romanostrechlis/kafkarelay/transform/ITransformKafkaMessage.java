package io.github.romanostrechlis.kafkarelay.transform;

public interface ITransformKafkaMessage {

    String transformToA(String messageFromB);
    String transformToB(String messageFromA);
}
