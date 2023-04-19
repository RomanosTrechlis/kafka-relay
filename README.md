# kafka-relay

**kafka-relay** is a spring-boot application that reads messages 
from a kafka cluster and forwards it to another kafka cluster.

```mermaid
flowchart TD;
kafka-relay(kafka-relay\n\nNetworks: clusterA, clusterB)
clusterA[Apache Kafka Cluster A\n\nNetworks: clusterA]
clusterB[Apache Kafka Cluster B\n\nNetworks: clusterB]

kafka-relay -.-> |Consumes/Produces| clusterA
kafka-relay -.-> |Consumes/Produces| clusterB
```

As the diagram suggests, the two Kafka clusters are isolated from each other.

The reason for **kafka-relay** existence is that it provides a decoupling between 
the main application and the integrations for third party applications.
That way, the main application knows how to read messages from the kafka cluster A, as well as their format.
The **kafka-relay** is responsible to read integration specific messages from cluster B, transform them in 
the format of the main application and forward them to the cluster A.

## Build

Build **kafka-relay** using gradle.

![img.png](docs/img/gradle.png)

Or run the command `gradlew build`.

## Deployment

After building **kafka-relay** we are ready to deploy.

By running `docker-compose up` a demo environment gets deployed.

```mermaid
flowchart TD;

kafka-relay(kafka-relay\n\nNetworks: clusterA, clusterB)

subgraph ClusterA[Apache Kafka Cluster A: clusterA]
    ZookeeperA[Zookeeper]
    KafkaBrokerA1[Kafka Broker 1]
    KafkaBrokerA2[Kafka Broker 2]
end

kafka-relay -.-> |Consume/Produce| ClusterA
kafka-relay -.-> |Consume/Produce| ClusterB

subgraph ClusterB[Apache Kafka Cluster B: clusterB]
    ZookeeperB[Zookeeper]
    KafkaBrokerB1[Kafka Broker 1]
    KafkaBrokerB2[Kafka Broker 2]
end
```

## Local demo

Run the following command to produce a message to Cluster B.

```
 sh kafka-console-producer.sh --bootstrap-server localhost:10003,localhost:10004 --topic topic
```

This creates a prompt to write the message:

![img.png](docs/img/kafka-producer-prompt.png)

And the following line in the kafka broker appears:

![img.png](docs/img/kafka-broker-log.png)

We then write a message to the prompt.

The relay listen to the topic and after transforming the message it sends it to
the other cluster. The new topic is created if it doesn't exist.

![img.png](docs/img/kafdrop_main.png)

Navigating into kafdrop topic we can read the message.

![img.png](docs/img/kafdrop_inner.png)

We can also check the relay's logs.

![img.png](docs/img/relay-logs.png)

## Configuration

The following properties must be filled in order for the application to run correctly.

```properties
kafka.clusterA.bootstrapAddress=kafka-clusterA
kafka.clusterA.listen.topic=topic
kafka.clusterA.send.topic=sendA
kafka.clusterA.groupId=test
kafka.clusterB.bootstrapAddress=kafka-clusterB
kafka.clusterB.listen.topic=topic
kafka.clusterB.send.topic=sendB
kafka.clusterB.groupId=test
```

Reference also to [docker-compose.yml](docker-compose.yml)

## Transform

The API exposes an interface `ITransformKafkaMessage` with two methods. 

+ String transformToA(String messageFromB);
+ String transformToB(String messageFromA);

The `DefaultKafkaMessageTransformer` returns the message without any actual transformation.

For a specific transformation, implement the `ITransformKafkaMessage` interface.

## Help

[Help.md](docs/HELP.md)

