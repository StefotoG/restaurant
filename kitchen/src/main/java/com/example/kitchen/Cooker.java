package com.example.kitchen;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.example.kitchen.exceptions.FailedToCookException;

@Component
public class Cooker {
    public String cookRandomMeal() throws FailedToCookException {
        List<String> ingredients = new ArrayList<>(List.of("CHICKEN READY", "BEEF READY", "LAST MONTH MAMMOTH READY"));

        try {
            Thread.sleep(5000);
            //random index between 0 and 2
            int meal = (int) (Math.random() * ingredients.size());
            return ingredients.get(meal);

        } catch (InterruptedException e) {
            throw new FailedToCookException("Failed to cook meal");
        }
    }
}
