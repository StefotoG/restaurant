package com.example;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

public class App {

    public static void main(String[] args) {
        final int MENU_SIZE = 3;
        WebClient webClient = createWebClient();
        KitchenClient kitchenClient = new KitchenClient(webClient);
        Set<String> orders = new HashSet<>();

        String order = kitchenClient.order();

        while (orders.size() < MENU_SIZE) {
            System.out.println("Received order: " + order);
            if (orders.add(order)) {
                System.out.println("Accepted order: " + order);
                kitchenClient.acceptOrder();
                order = kitchenClient.order();
            } else if (order.equals("CLOSED BYE")) {
                System.out.println("The restaurant is closed. Goodbye!");
                break;
            } else {
                System.out.println("rejecting order: " + order);
                order = kitchenClient.cancelOrder();
            }
        }
        System.out.println("orders: " + orders);
    }

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
}
