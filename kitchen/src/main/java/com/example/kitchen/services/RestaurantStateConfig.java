package com.example.kitchen.services;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.kitchen.services.RestaurantState.RestaurantStates;

@Configuration
public class RestaurantStateConfig {

    @Bean
    public RestaurantState restaurantState() {
        return new RestaurantState(RestaurantStates.IDLE);
    }
}