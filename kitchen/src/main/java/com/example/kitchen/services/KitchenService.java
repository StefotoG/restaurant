package com.example.kitchen.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.Cooker;
import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;
import com.example.kitchen.exceptions.FailedToCookException;
import com.example.kitchen.exceptions.ResponseFailedException;

@Service
public class KitchenService {
    private final ExecutorService bakers = Executors.newFixedThreadPool(5);
    private RestaurantState currentState = RestaurantState.IDLE;

    @Autowired
    private Cooker cooker;

    public SseEmitter handleClientRequest(KitchenRequest request) {
        SseEmitter emitter = new SseEmitter(5000L);
        Map<RestaurantState, Runnable> stateHandlers = Map.of(
                RestaurantState.IDLE, () -> handleKitchenRequest(request, emitter),
                RestaurantState.WAITING, () -> {
                    try {
                        sendResponse(emitter, "OK, WAIT");
                    } catch (IOException e) {
                        throw new ResponseFailedException("Failed to send response");
                    }
                },
                RestaurantState.SERVED, () -> serveMeal(request, emitter),
                RestaurantState.CLOSED, () -> {
                    try {
                        handleClosedBye(emitter);
                    } catch (IOException e) {
                        throw new ResponseFailedException("Failed to send response");
                    }
                }
        );

        bakers.submit(() -> {
            stateHandlers.get(currentState).run();
        });

        return emitter;
    }

    private void sendResponse(SseEmitter emitter, String response) throws IOException {
        emitter.send(SseEmitter.event().name("response").data(response));
        emitter.complete();
    }

    private void sendNotification(SseEmitter emitter, String notification) throws IOException {
        emitter.send(SseEmitter.event().name("notification").data(notification));
    }

    private void handleClosedBye(SseEmitter emitter) throws IOException {
        System.out.println("Server: CLOSED BYE");
        sendResponse(emitter, "CLOSED BYE");
    }

    private void serveMeal(KitchenRequest request, SseEmitter emitter) {
        Map<ClientCommand, Runnable> requestHandlers = Map.of(
                ClientCommand.I_TAKE_THAT, handleTakeCommand(emitter),
                ClientCommand.NO_THANKS, handleNoThanksCommand(emitter)
        );
        requestHandlers.getOrDefault(request.getCommand(), () -> {
            try {
                sendResponse(emitter, "INVALID REQUEST");
            } catch (IOException e) {
                throw new ResponseFailedException("Failed to send response");
            }
        }).run();
    }

    private Runnable handleNoThanksCommand(SseEmitter emitter) {
        return () -> {
            try {
                prepareMeal(emitter);
            } catch (IOException e) {
                throw new ResponseFailedException("Failed to send response");
            } catch (FailedToCookException e) {

            }
        };
    }

    private Runnable handleTakeCommand(SseEmitter emitter) {
        return () -> {
            try {
                handleServedRequest(emitter);
            } catch (IOException e) {
                throw new ResponseFailedException("Failed to send response");
            }
        };
    }

    private void handleKitchenRequest(KitchenRequest request, SseEmitter emitter) {
        Map<ClientCommand, Runnable> requestHandlers = Map.of(ClientCommand.HUNGRY, handleNoThanksCommand(emitter),
                ClientCommand.NO_THANKS, () -> {
                    try {
                        closeKitchen(emitter);
                    } catch (IOException e) {
                        throw new ResponseFailedException("Failed to send response");
                    }
                });

        requestHandlers.getOrDefault(request.getCommand(), () -> {
            try {
                sendResponse(emitter, "INVALID REQUEST");
            } catch (IOException e) {
                throw new ResponseFailedException("Failed to send response");
            }
        }).run();
    }

    private void closeKitchen(SseEmitter emitter) throws IOException {
        currentState = RestaurantState.CLOSED;
        sendResponse(emitter, "CLOSED BYE");
    }

    private void prepareMeal(SseEmitter emitter) throws IOException, FailedToCookException {
        currentState = RestaurantState.WAITING;
        System.out.println("Server: OK, WAIT");
        // Simulate server push notifications
        sendNotification(emitter, "OK, WAIT");
        String meal = cooker.cookRandomMeal();
        currentState = RestaurantState.SERVED;
        sendResponse(emitter, meal);
    }

    private void handleServedRequest(SseEmitter emitter) throws IOException {
        currentState = RestaurantState.IDLE; // Reset to IDLE after serving
        sendResponse(emitter, "SERVED BYE");
    }
}
