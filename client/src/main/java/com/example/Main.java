package com.example;

import java.util.concurrent.CountDownLatch;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

public class Main {

    public static void main(String[] args) {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host("localhost")
                .port(3128));

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        WebClient webClient = WebClient.builder()
        .clientConnector(connector)
        .baseUrl("http://127.0.0.1:8080").build();
        CountDownLatch latch = new CountDownLatch(1);

        KitchenClient kitchenClient = new KitchenClient(webClient, latch);
        System.out.println(kitchenClient.order());
    }
}
