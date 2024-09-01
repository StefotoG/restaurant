package com.example.kitchen.dtos.responses;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KitchenResponse {
    private final KitchenCommand command;
}
