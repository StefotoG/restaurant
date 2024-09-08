package com.example.kitchen.controllers;


import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.server.ResponseStatusException;

import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;
import com.example.kitchen.services.KitchenService;

@WebMvcTest(KitchenController.class)
public class KitchenControllerTest {

    @MockBean
    private KitchenService kitchenService;
    @Mock
    private KitchenRequest kitchenRequest;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void orderTest() throws Exception {
        when(kitchenRequest.getCommand()).thenReturn(ClientCommand.HUNGRY);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(kitchenRequest.getCommand().getValue()))
            .andExpect(MockMvcResultMatchers.status().isOk());

        ArgumentCaptor<KitchenRequest> captor = forClass(KitchenRequest.class);
        verify(kitchenService, times(1)).handleClientRequest(captor.capture());

        KitchenRequest capturedRequest = captor.getValue();
        assertThat(capturedRequest.getCommand()).isEqualTo(ClientCommand.HUNGRY);
    }

    @Test
    public void InvalidCommandTest() throws Exception {
        String invalidCommand = "INVALID";
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidCommand))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void MissingCommandTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/cook")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{}"))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }


    @Test
    public void errorHandlingTest() throws Exception {
        when(kitchenService.handleClientRequest(any(KitchenRequest.class)))
            .thenThrow(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to send response"));

        mockMvc.perform(MockMvcRequestBuilders.post("/api/cook")
            .contentType(MediaType.APPLICATION_JSON)
            .content(ClientCommand.HUNGRY.getValue()))
            .andExpect(MockMvcResultMatchers.status().isInternalServerError());
    }
}