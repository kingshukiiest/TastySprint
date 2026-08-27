package com.foodordering.cart.repository;

import com.foodordering.cart.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for CartItem operations.
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
