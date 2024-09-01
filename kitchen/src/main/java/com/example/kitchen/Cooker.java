package com.example.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.kitchen.exceptions.FailedToCookException;

@Component
public class Cooker {

    public String cookRandomMeal() throws FailedToCookException {
        try {
            Thread.sleep(5000);
            return getRandomMeal();

        } catch (InterruptedException e) {
            throw new FailedToCookException("Failed to cook meal");
        }
    }

    private String getRandomMeal() {
        List<String> ingredients = new ArrayList<>(List.of("CHICKEN READY", "BEEF READY", "LAST MONTH MAMMOTH READY"));
        int meal = (int) (Math.random() * ingredients.size());
        return ingredients.get(meal);
    }
}
