package io.github.romanostrechlis.kafkarelay.transform;

import org.springframework.stereotype.Component;

@Component
class DefaultKafkaMessageTransformer implements ITransformKafkaMessage{
    @Override
    public String transformToA(String messageFromB) {
        return messageFromB;
    }

    @Override
    public String transformToB(String messageFromA) {
        return messageFromA;
    }
}
