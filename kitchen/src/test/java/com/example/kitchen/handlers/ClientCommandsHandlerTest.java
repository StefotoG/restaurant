package com.example.kitchen.handlers;


import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.Cooker;
import com.example.kitchen.exceptions.FailedToCookException;
import com.example.kitchen.services.RestaurantState;
import com.example.kitchen.services.RestaurantState.RestaurantStates;

public class ClientCommandsHandlerTest {
    private static final String MEAL = "Meal";
    private ClientCommandsHandler clientCommandsHandler;

    @Mock
    private Cooker cooker;
    @Mock
    private RestaurantState currentState;
    private SseEmitter emitter;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        clientCommandsHandler = new ClientCommandsHandler(cooker, currentState);
        emitter = new SseEmitter();
    }

    @Test
    public void handleHungryCommand_ShouldPrepareMealAndSendResponseTest() throws IOException, FailedToCookException {
        when(cooker.cookRandomMeal()).thenReturn(MEAL);

        Runnable runnable = clientCommandsHandler.handleHungryCommand(emitter);
        runnable.run();

        verify(currentState).setCurrentState(RestaurantStates.WAITING);
        verify(currentState).setCurrentState(RestaurantStates.SERVED);
    }

    @Test
    public void handleHungryCommand_ShouldHandleFailedResponseTest() throws IOException, FailedToCookException {
        doThrow(FailedToCookException.class).when(cooker).cookRandomMeal();

        Runnable runnable = clientCommandsHandler.handleHungryCommand(emitter);
        assertThrows(ResponseStatusException.class, runnable::run);

        verify(currentState).setCurrentState(RestaurantStates.WAITING);
        verify(currentState).setCurrentState(RestaurantStates.IDLE);
    }

    @Test
    public void handleHungryCommand_ShouldHandleFailedToCookExceptionTest() throws IOException, FailedToCookException {
        when(cooker.cookRandomMeal()).thenThrow(FailedToCookException.class);

        Runnable runnable = clientCommandsHandler.handleHungryCommand(emitter);

        assertThrows(ResponseStatusException.class, runnable::run);

        verify(currentState).setCurrentState(RestaurantStates.WAITING);
        verify(currentState).setCurrentState(RestaurantStates.IDLE);
    }

    @Test
    public void handleTakeCommand_ShouldPrepareMealAndSendResponseTest() throws IOException, FailedToCookException {
        when(cooker.cookRandomMeal()).thenReturn(MEAL);

        Runnable runnable = clientCommandsHandler.handleTakeCommand(emitter);
        runnable.run();

        verify(currentState).setCurrentState(RestaurantStates.IDLE);
    }

    @Test
    public void handleInvalidCommand_ShouldSendResponseTest() throws IOException {
        Runnable runnable = clientCommandsHandler.handleInvalidCommand(emitter);
        assertThrows(ResponseStatusException.class, runnable::run);

        verify(currentState).setCurrentState(RestaurantStates.IDLE);
    }

    @Test
    public void handleNoThanksCommand_ShouldCloseKitchenTest() throws IOException {
        Runnable runnable = clientCommandsHandler.handleNoThanksCommand(emitter);
        runnable.run();

        verify(currentState).setCurrentState(RestaurantStates.CLOSED);
    }

    @Test
    public void handleFailedResponse_ShouldThrowResponseStatusExceptionTest() {
        assertThrows(ResponseStatusException.class, clientCommandsHandler::handleFailedResponse);
    }
}