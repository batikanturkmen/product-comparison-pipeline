package com.relayr.product.comparison.restproxyproductstreamprocessor.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ProductModel {

    @JsonProperty("category")
    String category;
    @JsonProperty("brand")
    String brand;
    @JsonProperty("product")
    String product;
    @JsonProperty("price")
    Float price;
    @JsonProperty("additional")
    Map<String, String> additional;

    public ProductModel() {
    }

    public ProductModel(String category, String brand, String product, Float price, Map<String, String> additional) {
        this.category = category;
        this.brand = brand;
        this.product = product;
        this.price = price;
        this.additional = additional;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public Map<String, String> getAdditional() {
        return additional;
    }

    public void setAdditional(Map<String, String> additional) {
        this.additional = additional;
    }

    @Override
    public String toString() {
        return "ProductModel{" +
                "category='" + category + '\'' +
                ", brand='" + brand + '\'' +
                ", product='" + product + '\'' +
                ", price=" + price +
                ", additional=" + additional +
                '}';
    }
}
