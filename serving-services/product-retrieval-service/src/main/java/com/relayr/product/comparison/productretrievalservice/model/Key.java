package com.relayr.product.comparison.productretrievalservice.model;

import org.springframework.data.cassandra.core.cql.Ordering;
import org.springframework.data.cassandra.core.cql.PrimaryKeyType;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyClass;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;

import java.io.Serializable;
import java.util.Objects;

@PrimaryKeyClass
public class Key implements Serializable {

    @PrimaryKeyColumn(name = "category", ordinal = 0, type = PrimaryKeyType.PARTITIONED)
    private String category;

    @PrimaryKeyColumn(name = "product", ordinal = 1, type = PrimaryKeyType.PARTITIONED)
    private String product;

    @PrimaryKeyColumn(name = "source", ordinal = 2, type = PrimaryKeyType.PARTITIONED)
    private String source;


    @PrimaryKeyColumn(name = "recommendation_score", ordinal = 3, type = PrimaryKeyType.CLUSTERED, ordering =  Ordering.ASCENDING)
    private Float score;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Float getScore() {
        return score;
    }

    public void setScore(Float score) {
        this.score = score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Key key = (Key) o;
        return Objects.equals(category, key.category) &&
                Objects.equals(product, key.product) &&
                Objects.equals(source, key.source) &&
                Objects.equals(score, key.score);
    }

    @Override
    public int hashCode() {
        return Objects.hash(category, product, source, score);
    }
}
