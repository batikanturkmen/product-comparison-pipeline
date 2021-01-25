package com.relayr.product.comparison.restproxyproductstreamprocessor.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relayr.product.comparison.restproxyproductstreamprocessor.model.ProductModel;
import com.relayr.product.models.Product;
import com.relayr.product.models.ProductKey;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class StreamEnricher {

    @Value("#{kafkaConfig.getStreamInputTopicName()}")
    private String streamInputTopicName;
    @Value("#{kafkaConfig.getStreamOutputTopicName()}")
    private String streamOutputTopicName;
    @Value("#{kafkaConfig.getDataSourceName()}")
    private String dataSourceName;

    private ObjectMapper mapper = new ObjectMapper();
    // declare mock machine learning model results for demonstration purpose.
    private static Map<String, Integer> mlScoreMap;
    // initialize mock machine learning model results for demonstration purpose.
    static {
        mlScoreMap = new HashMap<>();
        mlScoreMap.put("mediamarkt", 8);
        mlScoreMap.put("bestbuy", 7);
    }

    Logger logger = LoggerFactory.getLogger(StreamEnricher.class);

    @Bean
    public KStream<ProductKey, Product> kStream(StreamsBuilder kStreamBuilder) {

        KStream<ProductKey, Product> convertedStream = kStreamBuilder
                .stream(streamInputTopicName, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, value) -> this.checkValidity(value))
                .mapValues(this::valueTransformation)
                .mapValues(this::convertValueToAvro)
                .selectKey((key, value) -> this.convertKeyToAvro(value));

        convertedStream.to(streamOutputTopicName);

        return convertedStream;
    }

    protected boolean checkValidity(String value) {

        // check line emptiness
        if (value.trim().isEmpty()) {
            return false;
        }

        // if there is exception, filter out.
        if (!this.convertToModel(value).isPresent()){
            return false;
        }
        return true;
    }

    protected String valueTransformation(String value){

        return value
                .trim()
                .toLowerCase();
    }

    protected Optional<ProductModel> convertToModel(String value){
        Optional<ProductModel> productModel = Optional.empty();

        try {
            productModel = Optional.of(mapper.readValue(value, ProductModel.class));
        } catch (JsonProcessingException e) {
            if (logger.isWarnEnabled()){
                logger.warn(e.toString());
            }
        }

        return productModel;
    }

    protected ProductKey convertKeyToAvro(Product product) {

        return ProductKey
                .newBuilder()
                .setSource(dataSourceName)
                .setCategory(product.getCategory())
                .setBrand(product.getBrand())
                .setProduct(product.getProduct())
                .build();
    }

    protected Product convertValueToAvro(String value) {
        Optional<ProductModel> productModel = this.convertToModel(value);

        // get directly, because checked data validity in previous steps.
        return Product.newBuilder()
                .setSource(dataSourceName)
                .setCategory(productModel.get().getCategory())
                .setBrand(productModel.get().getBrand())
                .setProduct(productModel.get().getProduct())
                .setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .setId(UUID.randomUUID().toString())
                .setPrice(productModel.get().getPrice())
                .setRecommendationScore(mlScoreMap.getOrDefault(dataSourceName, 5))
                .setAdditional(this.convertMap(productModel.get().getAdditional()))
                .build();
    }

    protected String convertMap(Map<String, String> map) {
        return map.keySet().stream()
                .map(key -> "\"" + key + "\":\"" + map.get(key) + "\"")
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
