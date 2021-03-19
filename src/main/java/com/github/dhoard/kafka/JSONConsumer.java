package com.github.dhoard.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSONConsumer {

    private static final Logger logger = LoggerFactory.getLogger(JSONConsumer.class);

    private static final String BOOTSTRAP_SERVERS = "cp-5-5-x.address.cx:9092";

    private static final String TOPIC = "loan-group-complete";

    public static void main(String[] args) throws Exception {
        new JSONConsumer().run(args);
    }

    public void run(String[] args) throws Exception {
        KafkaConsumer<String, String> kafkaConsumer = null;

        try {
            Properties properties = new Properties();

            properties.setProperty(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);

            properties.setProperty(
                ConsumerConfig.CLIENT_ID_CONFIG, getClass().getName());

            logger.info(ConsumerConfig.CLIENT_ID_CONFIG + " = [" + getClass().getName() + "]");

            properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

            properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class.getName());

            properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,
                getClass().getName());

            properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            kafkaConsumer = new KafkaConsumer<String, String>(properties);
            kafkaConsumer.subscribe(Collections.singleton(TOPIC));

            while (true) {
                ConsumerRecords<String, String> consumerRecords =
                    kafkaConsumer.poll(Duration.ofMillis(250));

                if (consumerRecords.count() > 0) {
                    logger.info("message count = [" + consumerRecords.count() + "]");

                    consumerRecords.forEach(record -> {
                        String event = record.value();
                        logger.info("message(" + event + ")");
                    });

                    kafkaConsumer.commitAsync();
                }
            }
        } finally {
            if (null != kafkaConsumer) {
                try {
                    kafkaConsumer.close();
                } catch (Throwable t) {
                    // DO NOTHING
                }
            }
        }
    }
}
