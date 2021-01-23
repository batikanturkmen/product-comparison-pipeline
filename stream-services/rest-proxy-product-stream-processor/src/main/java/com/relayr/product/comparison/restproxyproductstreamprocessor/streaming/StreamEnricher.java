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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
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
                .selectKey((key, value) -> StreamEnricher.convertKeyToAvro(value))
                .mapValues(StreamEnricher::convertValueToAvro);


        convertedStream.to(streamOutputTopicName);

        return convertedStream;
    }

    private static boolean checkValidity(String value) {

        System.out.println("Gelen veri: " + value);

        // check line emptiness
        if (value.trim().isEmpty()) {
            return false;
        }

        return true;
    }


    // TODO belki interface yapilip commona alinabilir
    private static ProductKey convertKeyToAvro(String value) {

        ProductModel productModel = null;


        // TODO bu islemi iki kere yapmak mantikli olmayabilir
        // TODO ne doncen dusun
        try {
            productModel = mapper.readValue(value, ProductModel.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // TODO handle et duzgun sekilde
        }

        System.out.println("Donusturulen " + productModel.toString());

        String dataSourceName = "bestbuy"; //TODO

        return ProductKey
                .newBuilder()
                .setSource(dataSourceName)
                .setCategory(productModel.getCategory())
                .setBrand(productModel.getBrand())
                .setProduct(productModel.getProduct())
                .build();
    }

    private static Product convertValueToAvro(String value) {

        ProductModel productModel = null;


        // TODO bu islemi iki kere yapmak mantikli olmayabilir
        // TODO ne doncen dusun
        try {
            productModel = mapper.readValue(value, ProductModel.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace(); // TODO handle et duzgun sekilde
        }

        System.out.println("Donusturulen " + productModel.toString());


        String dataSourceName = "bestbuy"; //TODO

        return Product.newBuilder()
                .setSource(dataSourceName)
                .setCategory(productModel.getCategory())
                .setBrand(productModel.getBrand())
                .setProduct(productModel.getProduct())
                .setCreatedAt(ZonedDateTime.now().toInstant().toEpochMilli())
                .setId(UUID.randomUUID().toString())
                .setPrice(productModel.getPrice())
                .setRecommendationScore(mlScoreMap.getOrDefault(dataSourceName, 5))
                .setAdditional(StreamEnricher.converMap(productModel.getAdditional()))
                .build();
    }


    //TODO daha temiz cozebilirsen bak
    // TODO DB icin ayni donusumu yapiyor mu bak
    public static String converMap(Map<String, String> map) {
        String mapAsString = map.keySet().stream()
                .map(key -> key + "=" + map.get(key))
                .collect(Collectors.joining(", ", "{", "}"));
        return mapAsString;
    }
}
