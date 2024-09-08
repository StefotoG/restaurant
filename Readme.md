# Kitchen Order Management System

This project is a simple kitchen order management system implemented in Java using Spring WebFlux and Reactor Netty. The application interacts with a kitchen service to place and manage orders.

## Table of Contents

- [Introduction](#introduction)
- [Key Components](#key-components)
- [Client Application](#client-application)
- [Server Application](#server-application)
- [BBQ Protocol](#bbq-protocol)
- [Squid Proxy](#squid-proxy)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Usage](#usage)
- [Code Overview](#code-overview)

## Introduction

This project demonstrates a client-server application architecture that implements long polling with server-side push events over the HTTP protocol. The communication between the client and server is routed through a Squid application layer proxy. Additionally, the project implements a simple BBQ protocol, which defines specific commands for interaction between the client and server.

## Key Components

### Client Application

The client application is located in the [`client`](client) directory. It uses Spring WebFlux to handle HTTP requests and responses asynchronously. The main entry point for the client is the [`App`](client\src\main\java\com\example\App.java) class, which sets up the HTTP client and manages the order processing logic.

### Server Application

The server application resides in the [`kitchen`](kitchen) directory. It is built using Spring Boot and exposes REST endpoints to handle client requests. The main components include:

- [`KitchenController`](kitchen\src\main\java\com\example\kitchen\controllers\KitchenController.java): Handles incoming HTTP requests and delegates processing to the service layer.
- [`KitchenService`](kitchen\src\main\java\com\example\kitchen\services\KitchenService.java): Contains the business logic for processing client commands.
- [`ClientCommandsHandler`](kitchen\src\main\java\com\example\kitchen\handlers\ClientCommandsHandler.java): Manages the execution of client commands.

### BBQ Protocol

The BBQ protocol defines a set of commands for communication between the client and server. These commands are represented by enums in the project:

- [`ClientCommand`](kitchen\src\main\java\com\example\kitchen\dtos\requests\ClientCommand.java): Defines commands sent by the client.
- [`KitchenCommand`](kitchen\src\main\java\com\example\kitchen\dtos\requests\KitchenRequest.java): Defines responses from the server.

### Squid Proxy

The communication between the client and server is routed through a Squid proxy, configured in the [`squid.conf`](squid.conf) file. The client application sets up the proxy settings in the [`createWebClient`](client\src\main\java\com\example\App.java) method.

## Features

- Place orders to the kitchen service.
- Accept or reject orders based on predefined conditions.
- Handle the closing of the kitchen service.

## Prerequisites

- Java 17 or higher
- Maven 3.6.0 or higher

## Installation

1. Clone the repository:

   ```sh
   git clone git@github.com:StefotoG/restaurant.git
   cd restaurant
   ```

2. Build the project using Maven:
   ```sh
   mvn clean install
   ```

## Usage

1.  Start the server:

    ```sh
    cd kitchen
    java -jar target/kitchen-1.0-SNAPSHOT.jar

    ```

    The server will start and listen for incoming requests.

2.  Run the client application:

    ```sh
    cd ../client
    java -jar target/client-1.0-SNAPSHOT.jar
    ```

    The client application will start and interact with the kitchen service, placing and managing orders.

## Code Overview

### `App.java`

This is the main entry point of the application. It initializes the `WebClient` and `KitchenClient`, and manages the order lifecycle.

```java
import java.util.Set;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.transport.ProxyProvider;

public class App {

    public static void main(String[] args) {
        final int MENU_SIZE = 3;
        WebClient webClient = createWebClient();
        KitchenClient kitchenClient = new KitchenClient(webClient);
        Set<String> orders = new HashSet<>();

        String order = kitchenClient.order();

        while (orders.size() < MENU_SIZE) {
            System.out.println("Received order: " + order);
            if (orders.add(order)) {
                System.out.println("Accepted order: " + order);
                kitchenClient.acceptOrder();
                order = kitchenClient.order();
            } else if (order.equals("CLOSED BYE")) {
                System.out.println("The restaurant is closed. Goodbye!");
                break;
            } else {
                System.out.println("Rejecting order: " + order);
                order = kitchenClient.cancelOrder();
            }
        }
        System.out.println("Orders: " + orders);
    }
}
```

## Sequence Diagram

![sequence](https://www.plantuml.com/plantuml/svg/TPB1QiCm38RlVWgnKyhO2uHHMdeQOmoQmywU_TEQER9dEnrx--742qxQdcpB__TBIDh594CQUaiHtmDOGKYLNA23DU0an7JIqvRsthVKIyFD9clCHIRae11xxxFWY36C2Mc9yREArUJovww92f09DVY1py8ijPcU_ofGumRwd-xL97K6roKlGisCL1Ki5FUiV70ACRu7ZH2RQxf3k1W58QnpdWPEnfAqbdf86qa6a7Bk2reGqTAxIbPCMZfBrhOqufWd6LDJDEMvgOqAbvqEEKjensn7oThrRv2scUHpqPDgZ4tNZBxmAK3dbB6b4rBrBdpL-yhXkeXLx-RAetSS8S2w7kEwWrVT6LUrGthkKZ1ysf8Rft33OpN3xZScz7buXfFbvc42MtgWnsAoMAXn_NTWFVJs3m00 "sequence")