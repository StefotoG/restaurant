package com.example.connection;

import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

public class ReactiveClient {
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final int PROXY_PORT = 3128;
    private static final int MAX_CONNECTIONS = 1;

    public WebClient create() {
        HttpClient httpClient = HttpClient.create(ConnectionProvider.create("client", MAX_CONNECTIONS))
                .proxy(proxy -> proxy
                .type(ProxyProvider.Proxy.HTTP)
                .host(HOST)
                .port(PROXY_PORT)
                .nonProxyHosts(HOST + ":" + PORT)
                );

        ReactorClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);
        WebClient webClient = WebClient.builder()
                .clientConnector(connector)
                .baseUrl("http://127.0.0.1:8080").build();
        return webClient;
    }
}
