package com.compare.mq.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class RocketMQConfig {

    public static final String USER_TOPIC = "user-topic";
    public static final String USER_GROUP = "user-group";
    public static final String SHOP_TOPIC = "shop-topic";
    public static final String SHOP_GROUP = "shop-group";
    public static final String PRODUCT_TOPIC = "product-topic";
    public static final String PRODUCT_GROUP = "product-group";
    public static final String FAVORITE_TOPIC = "favorite-topic";
    public static final String FAVORITE_GROUP = "favorite-group";
}
