package org.example.bbqrestaurant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        String requestBodyString = "I AM HUNGRY, GIVE ME BBQ";
        HttpRequest hungryRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/kitchen/"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString(requestBodyString))
                .build();
        HttpRequest noThanksRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/kitchen/"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("NO THANKS"))
                .build();
        HttpRequest iTakeThatRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8000/kitchen/"))
                .header("Content-Type", "text/plain")
                .POST(HttpRequest.BodyPublishers.ofString("I TAKE THAT !!!"))
                .build();

        HttpClient client = HttpClient.newHttpClient();

        HttpResponse<String> response = client.send(iTakeThatRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        response = client.send(noThanksRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
        response = client.send(hungryRequest, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.body());
    }
}
