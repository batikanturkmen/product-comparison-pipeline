package com.relayr.product.comparison.restproxyproductstreamprocessor.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Value("${spring.kafka.stream.input.topic-name}")
    private String streamInputTopicName;

    @Value("${spring.kafka.stream.output.topic-name}")
    private String streamOutputTopicName;

    @Value("${pipeline.product.data.source}")
    private String dataSource;

    @Bean
    public String getStreamInputTopicName() {
        return streamInputTopicName;
    }

    @Bean
    public String getStreamOutputTopicName() {
        return streamOutputTopicName;
    }

    @Bean
    public String getDataSourceName() {
        return dataSource;
    }
}
