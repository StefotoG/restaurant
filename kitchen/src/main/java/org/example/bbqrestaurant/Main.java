package org.example.bbqrestaurant;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        BBQRestaurant bbqRestaurant = new BBQRestaurant();
        try {
            bbqRestaurant.start(6666);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}