package com.relayr.product.comparison.fileproductstreamprocessor.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.UUID;

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
        // check category, product and price data holders
        if (value.trim().split(",").length < 4) {
            return false;
        }
        return true;
    }

    protected String valueTransformation(String value){

        return value
                .trim()
                .replace("\"","")
                .toLowerCase();
    }

    protected ProductKey convertKeyToAvro(Product value) {

        return ProductKey
                .newBuilder()
                .setSource(dataSourceName)
                .setCategory(value.getCategory())
                .setBrand(value.getBrand())
                .setProduct(value.getProduct())
                .build();
    }

    protected Product convertValueToAvro(String value) {

        return Product.newBuilder()
                .setSource(dataSourceName)
                .setCategory(value.split(",")[0])
                .setBrand(value.split(",")[1])
                .setProduct(value.split(",")[2])
                .setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .setId(UUID.randomUUID().toString())
                .setPrice(Float.parseFloat(value.split(",")[3]))
                .setRecommendationScore(mlScoreMap.getOrDefault(dataSourceName, 5))
                .setAdditional(this.additionalDataParser(value))
                .build();
    }

    protected String additionalDataParser(String value) {

        // means no additional data
        if (value.split(",").length < 5) {
            return "";
        }

        // not symmetric key value pair
        if (value.split(",").length % 2 == 1) {
            return "";
        }

        Map<String, String> additionalInformation = new HashMap<>();

        for (int i=4; i<value.split(",").length; i= i+2){
            additionalInformation.put(value.split(",")[i],value.split(",")[i+1]);
        }

        String json = "";
        try {
            json = mapper.writeValueAsString(additionalInformation);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
