package com.example.kitchen.dtos.requests;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KitchenRequest {
    private final ClientCommand command;
}
