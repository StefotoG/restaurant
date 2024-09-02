package com.example.kitchen.services;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RestaurantState {

    public enum RestaurantStates {
        IDLE, WAITING, SERVED, CLOSED;
    }

    private RestaurantStates currentState;
}