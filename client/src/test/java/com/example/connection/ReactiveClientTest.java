package com.example.connection;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;



public class ReactiveClientTest {
    private ReactiveClient reactiveClient;

    @BeforeEach
    public void setup() {
        reactiveClient = new ReactiveClient();
    }

    @Test
    public void create_ShouldReturnWebClientTest() {
        WebClient webClient = reactiveClient.create();

        assertThat(webClient).isNotNull();
    }
}