package com.example;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

public class App {

    private static WebClient createWebClient() {
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create("client", 1))
                .proxy(proxy -> proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host("localhost")
                .port(3128)
                .nonProxyHosts("localhost:8080")
                );

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        WebClient webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl("http://127.0.0.1:8080").build();
        return webClient;
    }

    public static void main(String[] args) {
        System.Logger logger = System.getLogger(App.class.getName());
        final int MENU_SIZE = 3;
        WebClient webClient = createWebClient();
        CountDownLatch latch = new CountDownLatch(1);
        KitchenClient kitchenClient = new KitchenClient(webClient, latch);
        Set<String> orders = new HashSet<>();

        String order = kitchenClient.order();
        logger.log(System.Logger.Level.INFO, "Received event: " + order);

        while (orders.size() < MENU_SIZE) {
            if (orders.add(order)) {
                kitchenClient.acceptOrder();
                order = kitchenClient.order();
                logger.log(System.Logger.Level.INFO, "accepted event: " + order);
            } else if (order.equals("CLOSED BYE")) {
                break;
            } else {
                order = kitchenClient.cancelOrder();
                logger.log(System.Logger.Level.INFO, "canceled event: " + order);
            }
        }

        kitchenClient.cancelOrder();
    }

}
