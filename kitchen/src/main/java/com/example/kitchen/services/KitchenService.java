package com.example.kitchen.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;

@Service
public class KitchenService {

    private final ExecutorService bakers = Executors.newFixedThreadPool(5);
    private RestaurantState currentState = RestaurantState.IDLE;

    public SseEmitter handleClientRequest(KitchenRequest request) {
        SseEmitter emitter = new SseEmitter(5000L);
        Map<RestaurantState, Runnable> stateHandlers = Map.of(
                RestaurantState.IDLE, () -> handleKitchenRequest(request, emitter),
                RestaurantState.WAITING, () -> sendResponse(emitter, "OK, WAIT"),
                RestaurantState.SERVED, () -> serveMeal(request, emitter),
                RestaurantState.CLOSED, () -> handleClosedBye(emitter)
        );

        bakers.submit(() -> {
            stateHandlers.get(currentState).run();
        });

        return emitter;
    }

    private void sendResponse(SseEmitter emitter, String response) {
        try {
            emitter.send(SseEmitter.event().name("response").data(response));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        emitter.complete();
    }

    private void sendNotification(SseEmitter emitter, String notification) {
        try {
            emitter.send(SseEmitter.event().name("notification").data(notification));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void handleClosedBye(SseEmitter emitter) {
        System.out.println("Server: CLOSED BYE");
        sendResponse(emitter, "CLOSED BYE");
    }

    private void serveMeal(KitchenRequest request, SseEmitter emitter) {
        Map<ClientCommand, Runnable> requestHandlers = Map.of(
                ClientCommand.I_TAKE_THAT, () -> handleServedRequest(request, emitter),
                ClientCommand.NO_THANKS, () -> prepareMeal(emitter)
        );
        requestHandlers.getOrDefault(request.getCommand(), () -> sendResponse(emitter, "INVALID REQUEST")).run();
    }

    private void handleKitchenRequest(KitchenRequest request, SseEmitter emitter) {
        Map<ClientCommand, Runnable> requestHandlers = Map.of(
                ClientCommand.HUNGRY, () -> prepareMeal(emitter),
                ClientCommand.NO_THANKS, () -> closeKitchen(emitter));

        requestHandlers.getOrDefault(request.getCommand(), () -> sendResponse(emitter, "INVALID REQUEST")).run();
    }

    private void closeKitchen(SseEmitter emitter) {
        currentState = RestaurantState.CLOSED;
        sendResponse(emitter, "CLOSED BYE");
    }

    private void prepareMeal(SseEmitter emitter) {
        currentState = RestaurantState.WAITING;
        System.out.println("Server: OK, WAIT");
        // Simulate server push notifications
        sendNotification(emitter, "OK, WAIT");
        String meal = cook();
        currentState = RestaurantState.SERVED;
        sendResponse(emitter, meal);
    }

    private String cook() {
        List<String> ingredients = new ArrayList<>(List.of("CHICKEN READY", "BEEF READY", "LAST MONTH MAMMOTH READY"));

        try {
            Thread.sleep(5000);
            //random index between 0 and 2
            int meal = (int) (Math.random() * ingredients.size());
            return ingredients.get(meal);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void handleServedRequest(KitchenRequest request, SseEmitter emitter) {
        currentState = RestaurantState.IDLE; // Reset to IDLE after serving
        sendResponse(emitter, "SERVED BYE");
    }
}
