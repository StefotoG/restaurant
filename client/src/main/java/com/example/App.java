package com.example;

import java.util.Set;

import org.springframework.web.reactive.function.client.WebClient;

public class App {
    public static void main(String[] args) {
        ReactiveClient reactiveClient = new ReactiveClient();
        WebClient webClient = reactiveClient.create();
        KitchenClient kitchenClient = new KitchenClient(webClient);
        Set<String> orders = kitchenClient.processOrders();
        System.out.println("orders: " + orders);
    }
}
