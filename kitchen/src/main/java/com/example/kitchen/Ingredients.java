package com.example.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Component
public class Ingredients {
    private final List<String> ingredients = new ArrayList<>(List.of("CHICKEN READY", "BEEF READY", "LAST MONTH MAMMOTH READY"));
}
