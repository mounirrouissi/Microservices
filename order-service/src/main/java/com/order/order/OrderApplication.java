package com.order.order;

import com.order.order.model.Order;
import com.order.order.model.OrderLine;
import com.order.order.repos.OrderLineRepo;
import com.order.order.repos.OrderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;
import java.util.UUID;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderApplication  {
@Autowired
	OrderRepo orderRepo;
@Autowired
	OrderLineRepo orderLineRepo;
	public static void main(String[] args) {
		SpringApplication.run(OrderApplication.class, args);
	}



	}

