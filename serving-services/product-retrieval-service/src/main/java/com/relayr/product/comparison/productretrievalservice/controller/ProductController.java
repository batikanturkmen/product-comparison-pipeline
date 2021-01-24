package com.relayr.product.comparison.productretrievalservice.controller;

import com.relayr.product.comparison.productretrievalservice.model.ProductModel;
import com.relayr.product.comparison.productretrievalservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/compare/category/{category}/product/{product}")
    public List<ProductModel> getProductByBrand(
            @PathVariable("category") String category,
            @PathVariable("product") String product) {
        return productRepository.findByKeyCategoryAndKeyProduct(category,product);
    }

}
