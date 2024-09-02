package com.example.kitchen.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.example.kitchen.dtos.requests.ClientCommand;
import com.example.kitchen.dtos.requests.KitchenRequest;
import com.example.kitchen.services.KitchenService;

@RestController
public class KitchenController {
    @Autowired
    KitchenService kitchenService;

    @RequestMapping(value = "/api/cook", method = RequestMethod.POST)
    public SseEmitter order(@RequestBody String request) {
        ClientCommand command = ClientCommand.fromValue(request.trim());
        return kitchenService.handleClientRequest(new KitchenRequest(command));
    }
}
