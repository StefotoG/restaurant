package org.example.bbqrestaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class BBQRestaurant implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            String requestBodyString = getRequestBodyString(exchange);

            try {
                handleRequestBody(exchange, requestBodyString);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            handleBadRequest(exchange);
        }
    }

    private void handleRequestBody(HttpExchange exchange, String requestBodyString) throws IOException, InterruptedException {
        if (requestBodyString.equals("I AM HUNGRY, GIVE ME BBQ")) {
            prepareBBQ(exchange);
        } else if (requestBodyString.equals("NO THANKS")) {
            // Send the response
            String response = "OK, BYE";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else if (requestBodyString.contains("I TAKE THAT !!!")) {
            // Send the response
            String response = "SERVED BYE";
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        } else {
            handleBadRequest(exchange);
        }
    }

    private void prepareBBQ(HttpExchange exchange) throws IOException, InterruptedException {
        // Send the response
        String response = "OK, WAIT";
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        Thread.sleep(10000);
        response = "BBQ IS READY";
        os.write(response.getBytes());
        os.close();
    }

    private String getRequestBodyString(HttpExchange exchange) throws IOException {
        // Read the request body
        InputStream requestBody = exchange.getRequestBody();
        BufferedReader reader = new BufferedReader(new InputStreamReader(requestBody));
        StringBuilder requestBodyBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            requestBodyBuilder.append(line);
        }
        String requestBodyString = requestBodyBuilder.toString();
        return requestBodyString;
    }

    private void handleBadRequest(HttpExchange exchange) throws IOException {
        // Send the response
        String response = "Sorry, I don't understand your request!";
        exchange.sendResponseHeaders(400, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}
