package com.relayr.product.comparison.productretrievalservice.model;

import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Table("products")
public class ProductModel implements Serializable {

    @PrimaryKey
    private Key pk;

    @Column
    private String additional;

    @Column
    private String brand;

    @Column
    private String id;

    @Column
    private Double price;

    public ProductModel(){

    }

    public ProductModel(Key pk, String additional, String brand, String id, Double price) {
        this.pk = pk;
        this.additional = additional;
        this.brand = brand;
        this.id = id;
        this.price = price;
    }

    public Key getPk() {
        return pk;
    }

    public void setPk(Key pk) {
        this.pk = pk;
    }

    public String getAdditional() {
        return additional;
    }

    public void setAdditional(String additional) {
        this.additional = additional;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

}
