package org.example.bbqrestaurant;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BBQRestaurant {

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    public void start(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            if ("I AM HUNGRY, GIVE ME BBQ".equals(inputLine)) {
                out.println("OK, WAIT");
                // Simulate server push events
                Executors.newSingleThreadScheduledExecutor().schedule(() -> out.println("CHICKEN READY"), 5, TimeUnit.SECONDS);
                Executors.newSingleThreadScheduledExecutor().schedule(() -> out.println("BEEF READY"), 10, TimeUnit.SECONDS);
                Executors.newSingleThreadScheduledExecutor().schedule(() -> out.println("LAST MONTH MAMMOTH READY"), 15, TimeUnit.SECONDS);
            } else if ("NO THANKS".equals(inputLine)) {
                out.println("CLOSED BYE");
                break;
            } else if ("I TAKE THAT!!!".equals(inputLine)) {
                out.println("SERVED BYE");
                break;
            }
        }

        stop();
    }

    public void stop() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
        serverSocket.close();
    }
}
