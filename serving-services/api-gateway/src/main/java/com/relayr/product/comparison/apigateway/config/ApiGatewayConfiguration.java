package com.relayr.product.comparison.apigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiGatewayConfiguration {

    @Bean
    public RouteLocator gatewayRouter(RouteLocatorBuilder builder) {
        return builder.routes()
                .route(p -> p.path("/product-retrieval-service/**")
                        .filters(f -> f.rewritePath(
                                "/product-retrieval-service/(?<segment>.*)",
                                "/${segment}"))
                        .uri("lb://product-retrieval-service"))
                .build();
    }
}
