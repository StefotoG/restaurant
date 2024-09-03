package com.example.kitchen.handlers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.Cooker;
import com.example.kitchen.exceptions.FailedToCookException;
import com.example.kitchen.services.RestaurantState;
import com.example.kitchen.services.RestaurantState.RestaurantStates;

@Component
public class ClientCommandsHandler {

    @Autowired
    private Cooker cooker;
    @Autowired
    private RestaurantState currentState;

    public Runnable handleHungryCommand(SseEmitter emitter) {
        return () -> {
            try {
                System.out.println("Server: client command handler hungry");
                prepareMeal(emitter);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response");
            } catch (FailedToCookException e) {

            }
        };
    }

    public Runnable handleTakeCommand(SseEmitter emitter) {
        return () -> {
            try {
                handleServedRequest(emitter);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response");
            }
        };
    }

    public Runnable handleInvalidCommand(SseEmitter emitter) {
        return () -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid command");
        };
    }

    public Runnable handleNoThanksCommand(SseEmitter emitter) {
        return () -> {
            try {
                closeKitchen(emitter);
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response");

            }
        };
    }

    private void closeKitchen(SseEmitter emitter) throws IOException {
        currentState.setCurrentState(RestaurantStates.CLOSED);
        sendResponse(emitter, "CLOSED BYE");
    }

    private void prepareMeal(SseEmitter emitter) throws IOException, FailedToCookException {
        currentState.setCurrentState(RestaurantStates.WAITING);
        System.out.println("Server: OK, WAIT");
        // Simulate server push notifications
        sendNotification(emitter, "OK, WAIT");
        String meal = cooker.cookRandomMeal();
        currentState.setCurrentState(RestaurantStates.SERVED);
        sendResponse(emitter, meal);
    }

    private void handleServedRequest(SseEmitter emitter) throws IOException {
        currentState.setCurrentState(RestaurantStates.IDLE); // Reset to IDLE after serving
        sendResponse(emitter, "SERVED BYE");
    }

    private void sendNotification(SseEmitter emitter, String notification) throws IOException {
        emitter.send(SseEmitter.event().name("notification").data(notification));
    }

    private void sendResponse(SseEmitter emitter, String response) throws IOException {
        emitter.send(SseEmitter.event().name("response").data(response));
        emitter.complete();
    }
}
