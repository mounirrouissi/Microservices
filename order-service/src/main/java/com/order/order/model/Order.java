package com.order.order.model;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String orderNumber;
    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST
            , orphanRemoval = true)
    private List<OrderLine> orderLines;

    public Order() {
        this.orderLines = new ArrayList<>();  // Initialize here
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public List<OrderLine> getOrderLines() {
        return orderLines;
    }

    public void setOrderLines(List<OrderLine> orderLines) {
        orderLines.forEach(orderLine -> orderLine.setOrder(this));
        this.orderLines = orderLines;
    }
}
