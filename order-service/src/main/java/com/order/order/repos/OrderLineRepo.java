package com.order.order.repos;

import com.order.order.model.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineRepo extends JpaRepository<OrderLine,Long> {
}
