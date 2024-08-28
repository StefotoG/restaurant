package org.example.bbqrestaurant;

import java.io.IOException;

import org.example.bbqrestaurant.client.BBQClient;

public class Main {

    public static void main(String[] args) throws IOException {
        BBQClient client = new BBQClient();
        client.startConnection("127.0.0.1", 6666);
        System.out.println(client.sendMessage("I AM HUNGRY, GIVE ME BBQ"));
        System.out.println(client.sendMessage("I TAKE THAT!!!"));
        client.stopConnection();
    }
}
