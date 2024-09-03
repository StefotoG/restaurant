package com.example.kitchen.services;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;
import com.example.kitchen.handlers.ClientCommandsHandler;
import com.example.kitchen.services.RestaurantState.RestaurantStates;

@Service
public class KitchenService {
    private final ExecutorService bakers = Executors.newFixedThreadPool(1);

    @Autowired
    private RestaurantState currentState;
    @Autowired
    private ClientCommandsHandler clientCommandsHandler;

    public SseEmitter handleClientRequest(KitchenRequest request) {
        SseEmitter emitter = new SseEmitter(5000L);
        Map<RestaurantStates, Runnable> stateHandlers = Map.of(
                RestaurantStates.IDLE, () -> handleKitchenRequest(request, emitter),
                RestaurantStates.WAITING, () -> {
                    try {
                        sendResponse(emitter, "OK, WAIT");
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response");
                    }
                },
                RestaurantStates.SERVED, () -> serveMeal(request, emitter),
                RestaurantStates.CLOSED, () -> {
                    try {
                        handleClosedBye(emitter);
                    } catch (IOException e) {
                        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response");
                    }
                }
        );

        bakers.submit(() -> {
            stateHandlers.get(currentState.getCurrentState()).run();
        });

        return emitter;
    }

    private void handleClosedBye(SseEmitter emitter) throws IOException {
        System.out.println("Server: CLOSED BYE");
        sendResponse(emitter, "CLOSED BYE");
    }

    private void serveMeal(KitchenRequest request, SseEmitter emitter) {
        Map<ClientCommand, Runnable> requestHandlers = Map.of(
                ClientCommand.I_TAKE_THAT, clientCommandsHandler.handleTakeCommand(emitter),
                ClientCommand.NO_THANKS, clientCommandsHandler.handleHungryCommand(emitter)
        );
        requestHandlers.getOrDefault(request.getCommand(), () -> clientCommandsHandler.handleInvalidCommand(emitter).run()).run();
    }

    private void handleKitchenRequest(KitchenRequest request, SseEmitter emitter) {
        System.out.println("executing handleKitchenRequest");
        Map<ClientCommand, Runnable> requestHandlers = Map.of(
                ClientCommand.HUNGRY, clientCommandsHandler.handleHungryCommand(emitter),
                ClientCommand.NO_THANKS, clientCommandsHandler.handleNoThanksCommand(emitter));

        requestHandlers.getOrDefault(request.getCommand(), () -> clientCommandsHandler.handleInvalidCommand(emitter).run()).run();
    }

    private void sendResponse(SseEmitter emitter, String response) throws IOException {
        emitter.send(SseEmitter.event().name("response").data(response));
        emitter.complete();
    }
}
