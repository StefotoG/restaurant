package com.example.kitchen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.kitchen.exceptions.FailedToCookException;

@Component
public class Cooker {

    @Autowired
    Ingredients ingredients;

    public String cookRandomMeal() throws FailedToCookException {
        try {
            Thread.sleep(5000);
            return getRandomMeal();

        } catch (InterruptedException e) {
            throw new FailedToCookException("Failed to cook meal");
        }
    }

    private String getRandomMeal() {
        int meal = (int) (Math.random() * ingredients.getIngredients().size());
        return ingredients.getIngredients().get(meal);
    }
}
