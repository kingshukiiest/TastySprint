package com.foodordering.cart.service;

import com.foodordering.cart.dto.AddToCartRequest;
import com.foodordering.cart.dto.CartResponse;
import com.foodordering.cart.dto.UpdateCartItemRequest;
import com.foodordering.cart.entity.Cart;

/**
 * Service interface for Shopping Cart domain logic.
 */
public interface CartService {

    CartResponse getCartByUserEmail(String userEmail);

    CartResponse addItemToCart(AddToCartRequest request, String userEmail);

    CartResponse updateCartItemQuantity(Long itemId, UpdateCartItemRequest request, String userEmail);

    CartResponse removeItemFromCart(Long itemId, String userEmail);

    void clearCart(String userEmail);

    Cart getOrCreateCartEntity(String userEmail);
}
