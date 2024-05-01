package com.order.order.services;

import com.netflix.discovery.converters.Auto;
import com.order.order.dto.OrderLineDto;
import com.order.order.dto.OrderRequest;
import com.order.order.event.OrderPlaceEvent;
import com.order.order.model.InventoryResponse;
import com.order.order.model.Order;
import com.order.order.model.OrderLine;
import com.order.order.repos.OrderLineRepo;
import com.order.order.repos.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {
    private static final String inventoryUri = "http://localhost:8084/api/inventory";
    private OrderRepo orderController;
    private WebClient webClient;
    private OrderRepo orderRepository;
    private final KafkaTemplate<Object, OrderPlaceEvent> kafkaTemplate;
  @Autowired
    private OrderLineRepo orderLineRepo;

    public OrderService(OrderRepo orderController, WebClient webClient, OrderRepo orderRepository, KafkaTemplate kafkaTemplate) {
        this.orderController = orderController;
        this.webClient = webClient;
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    public InventoryResponse[] isSkuAvailable(List<String> skuCodes) {
        //call inventory serivce using webClient
        return webClient.get()
                .uri(inventoryUri ,uriBuilder->uriBuilder.queryParam("skuCode",skuCodes).build())
                .retrieve()
                .bodyToMono(InventoryResponse[].class)
                .block();


    }
    public ResponseEntity<?> placeOrder(OrderRequest orderRequest) {

        List<OrderLine> orderLineList =
                orderRequest.orderLines().stream().map(this::mapToModel).toList();

        //create order
        var order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());

        kafkaTemplate.send("notificationTopic",new OrderPlaceEvent(order.getOrderNumber()));
        var skuCodes = orderLineList.stream().map(OrderLine::getSkuCode).toList();

        //save order if skuAvailable
        InventoryResponse[] skuAvailable = isSkuAvailable(skuCodes);
        if (skuAvailable.length >0 && Arrays.stream(skuAvailable).allMatch(InventoryResponse::isInStock)) {
            order.setOrderLines(orderLineList);
            orderRepository.save(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(skuAvailable);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(order);
    }

    private OrderLine mapToModel(OrderLineDto orderLineDto) {
    return new OrderLine(orderLineDto.id(),  orderLineDto.skuCode(), orderLineDto.price(), orderLineDto.quantity());
    }

    public ResponseEntity<?> getAllOrders() {
    return  ResponseEntity.ok(orderRepository.findAll());
    }
}
