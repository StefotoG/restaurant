package com.example;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import reactor.core.publisher.Flux;

public class KitchenClient {

    public KitchenClient(WebClient webClient, CountDownLatch latch) {
        this.webClient = webClient;
        this.latch = latch;
    }
    private final WebClient webClient;
    private final CountDownLatch latch;

    public String order() {
        StringBuilder result = new StringBuilder();

        Flux<String> eventStream = webClient.post()
                .uri("/api/cook")
                .bodyValue("I AM HUNGRY, GIVE ME BBQ")
                .retrieve()
                .bodyToFlux(String.class)
                .timeout(Duration.ofSeconds(10));

        eventStream.subscribe(
                event -> {
                    System.out.println("Received event: " + event);
                    result.append(event).append("\n");
                },
                error -> {
                    if (error instanceof WebClientResponseException webClientResponseException) {
                        result.append("Error: ").append(webClientResponseException.getResponseBodyAsString());
                    } else {
                        result.append("Error: ").append(error.getMessage());
                    }
                    latch.countDown();
                },
                latch::countDown
        );

        try {
            latch.await();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result.toString();
    }

}
