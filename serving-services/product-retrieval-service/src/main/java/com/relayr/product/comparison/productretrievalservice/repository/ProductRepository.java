package com.relayr.product.comparison.productretrievalservice.repository;


import com.relayr.product.comparison.productretrievalservice.model.ProductModel;
import com.relayr.product.comparison.productretrievalservice.model.Key;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;

import java.util.List;

public interface ProductRepository extends CassandraRepository<ProductModel, Key> {

    @Query("select * from products where category = ?0 and product = ?1 ALLOW FILTERING")
    List<ProductModel> findByKeyCategoryAndKeyProduct(String category, String product);

}
