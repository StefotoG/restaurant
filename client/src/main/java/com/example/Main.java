package com.example;

import java.util.concurrent.CountDownLatch;

import org.springframework.web.reactive.function.client.WebClient;

public class Main {

    public static void main(String[] args) {
        WebClient webClient = WebClient.create("http://127.0.0.1:8080");
        CountDownLatch latch = new CountDownLatch(1);
        
        KitchenClient kitchenClient = new KitchenClient(webClient, latch);
        System.out.println(kitchenClient.callBakeWithWebClient());
    }
}
