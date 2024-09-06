package com.example;

import java.time.Duration;
import java.util.function.Consumer;

import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

public class KitchenClient {
    public KitchenClient(WebClient webClient) {
        this.webClient = webClient;
    }
    private final WebClient webClient;

    public String order() {
        StringBuilder result = new StringBuilder();

        webClient.post()
                .uri("/api/cook")
                .bodyValue("I AM HUNGRY, GIVE ME BBQ")
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(handleResult(result))
                .doOnError(handleError(result))
                .blockLast(Duration.ofSeconds(10));

        return result.toString();
    }

    void acceptOrder() {
        webClient.post()
                .uri("/api/cook")
                .bodyValue("I TAKE THAT!!!")
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(handleAcceptOrderResult())
                .doOnError(handleError())
                .blockLast(Duration.ofSeconds(10));
    }

    String cancelOrder() {
        StringBuilder result = new StringBuilder();

        webClient.post()
                .uri("/api/cook")
                .bodyValue("NO THANKS")
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(handleResult(result))
                .doOnError(handleError(result))
                .blockLast(Duration.ofSeconds(10));

        return result.toString();
    }

    private Consumer<? super String> handleAcceptOrderResult() {
        return event -> {
            if (event.equals("SERVED BYE")) {
                return;
            }
            System.out.println("Received event: " + event);
        };
    }

    private Consumer<? super String> handleResult(StringBuilder result) {
        return event -> {
            if (event.equals("OK, WAIT")) {
                System.out.println("Received event: " + event);
                return;
            }
            System.out.println("Received event: " + event);
            result.append(event).append("\n");
        };
    }

    private Consumer<Throwable> handleError() {
        return handleError(new StringBuilder());
    }

    private Consumer<Throwable> handleError(StringBuilder result) {
        return error -> {
            if (error instanceof WebClientResponseException webClientResponseException) {
                result.append("Error: ").append(webClientResponseException.getResponseBodyAsString());
            } else {
                result.append("Error: ").append(error.getMessage());
            }
        };
    }
}
