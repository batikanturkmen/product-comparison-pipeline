package com.relayr.product.comparison.fileproductstreamprocessor.streaming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.relayr.product.models.Product;
import com.relayr.product.models.ProductKey;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.Consumed;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.ValueMapper;
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

    // declare mock machine learning model results for demonstration purpose.
    private static Map<String, Integer> mlScoreMap;

    // initialize mock machine learning model results for demonstration purpose.
    static {
        mlScoreMap = new HashMap<>();
        mlScoreMap.put("mediamarkt", 8);
        mlScoreMap.put("bestbuy", 7);
    }

    private static ObjectMapper mapper = new ObjectMapper(); // TODO bunu bean vs bır sey yap

    // TODO extra verilerde neler olmali ve nasil store edilmeli bak
    @Bean
    public KStream<ProductKey, Product> kStream(StreamsBuilder kStreamBuilder) {

        KStream<ProductKey, Product> convertedStream = kStreamBuilder
                .stream(streamInputTopicName, Consumed.with(Serdes.String(), Serdes.String()))
                .filter((key, value) -> StreamEnricher.checkValidity(value))
                .mapValues(StreamEnricher::valueTransformation)
                .selectKey((key, value) -> StreamEnricher.convertKeyToAvro(value))
                .mapValues(StreamEnricher::convertValueToAvro);

        convertedStream.to(streamOutputTopicName);

        return convertedStream;
    }

    private static boolean checkValidity(String value) {

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

    private static String valueTransformation(String value){
        return value
                .trim()
                .replace("\"","")
                .toLowerCase();
    }

    private static ProductKey convertKeyToAvro(String value) {

        String dataSourceName = "mediamarkt"; //TODO

        return ProductKey
                .newBuilder()
                .setSource(dataSourceName)
                .setCategory(value.split(",")[0])
                .setBrand(value.split(",")[1])
                .setProduct(value.split(",")[2])
                .build();
    }

    private static Product convertValueToAvro(String value) {
        String dataSourceName = "mediamarkt"; //TODO

        return Product.newBuilder()
                .setSource(dataSourceName)
                .setCategory(value.split(",")[0])
                .setBrand(value.split(",")[1])
                .setProduct(value.split(",")[2])
                .setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .setId(UUID.randomUUID().toString())
                .setPrice(Float.parseFloat(value.split(",")[3]))
                .setRecommendationScore(mlScoreMap.getOrDefault(dataSourceName, 5))
                .setAdditional(StreamEnricher.additionalDataParser(value))
                .build();
    }


    //TODO daha temiz cozebilirsen bak
    private static String additionalDataParser(String value) {

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

        // TODO springe uygun hale getir
        String json = "";
        try {
            json = mapper.writeValueAsString(additionalInformation);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return json;
    }
}
