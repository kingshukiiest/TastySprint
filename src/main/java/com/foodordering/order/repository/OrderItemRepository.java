package com.foodordering.order.repository;

import com.foodordering.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for OrderItem query operations.
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
