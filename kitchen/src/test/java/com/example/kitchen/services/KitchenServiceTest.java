package com.example.kitchen.services;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;
import com.example.kitchen.handlers.ClientCommandsHandler;
import com.example.kitchen.services.RestaurantState.RestaurantStates;


public class KitchenServiceTest {
    private KitchenService kitchenService;

    @Mock
    private RestaurantState currentState;
    @Mock
    private ClientCommandsHandler clientCommandsHandler;


    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        kitchenService = new KitchenService(currentState, clientCommandsHandler);
    }

    @Test
    public void handleClientRequest_ShouldReturnSseEmitterTest() {
        KitchenRequest request = new KitchenRequest(ClientCommand.HUNGRY);
        when(currentState.getCurrentState()).thenReturn(RestaurantStates.IDLE);

        SseEmitter result = kitchenService.handleClientRequest(request);

        assertThat(result).isNotNull();
    }

    @Test
    public void handleClientRequest_ShouldHandleKitchenRequestWhenStateIsIdleTest() {
        KitchenRequest request = new KitchenRequest(ClientCommand.HUNGRY);
        when(currentState.getCurrentState()).thenReturn(RestaurantStates.IDLE);

        kitchenService.handleClientRequest(request);

        verify(clientCommandsHandler, timeout(5000)).handleHungryCommand(Mockito.any());
    }

    @Test
    public void handleClientRequest_ShouldSendWaitingResponseWhenStateIsWaitingTest() throws IOException {
        KitchenRequest request = new KitchenRequest(null);
        when(currentState.getCurrentState()).thenReturn(RestaurantStates.WAITING);

        SseEmitter result = kitchenService.handleClientRequest(request);

        assertThat(result).isNotNull();
    }

    @Test
    public void handleClientRequest_ShouldServeMealWhenStateIsServedTest() {
        KitchenRequest request = new KitchenRequest(ClientCommand.I_TAKE_THAT);
        when(currentState.getCurrentState()).thenReturn(RestaurantStates.SERVED);

        kitchenService.handleClientRequest(request);

        verify(clientCommandsHandler, timeout(5000)).handleTakeCommand(Mockito.any());
    }

    @Test
    public void handleClientRequest_ShouldHandleClosedByeWhenStateIsClosedTest() throws IOException {
        KitchenRequest request = new KitchenRequest(ClientCommand.NO_THANKS);
        when(currentState.getCurrentState()).thenReturn(RestaurantStates.CLOSED);

        kitchenService.handleClientRequest(request);

        verify(clientCommandsHandler, times(0)).handleFailedResponse();
    }
}