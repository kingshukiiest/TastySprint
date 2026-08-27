package com.foodordering.cart.entity;

import com.foodordering.food.entity.Food;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entity representing an individual line item inside a shopping cart.
 */
@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "food_id", nullable = false)
    private Food food;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Double price;

    public void updatePrice() {
        if (food != null && quantity != null) {
            this.price = food.getPrice() * quantity;
        }
    }
}
