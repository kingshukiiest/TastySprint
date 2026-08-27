package com.foodordering.cart.controller;

import com.foodordering.cart.dto.AddToCartRequest;
import com.foodordering.cart.dto.CartResponse;
import com.foodordering.cart.dto.UpdateCartItemRequest;
import com.foodordering.cart.service.CartService;
import com.foodordering.common.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Controller exposing REST endpoints for managing the user's shopping cart.
 */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public ResponseEntity<ApiResponse<CartResponse>> getCart(Authentication authentication) {
        String userEmail = authentication.getName();
        CartResponse response = cartService.getCartByUserEmail(userEmail);
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved successfully", response));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartResponse>> addItemToCart(
            Authentication authentication,
            @Valid @RequestBody AddToCartRequest request) {
        String userEmail = authentication.getName();
        CartResponse response = cartService.addItemToCart(request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Item added to cart successfully", response));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> updateCartItem(
            @PathVariable Long itemId,
            Authentication authentication,
            @Valid @RequestBody UpdateCartItemRequest request) {
        String userEmail = authentication.getName();
        CartResponse response = cartService.updateCartItemQuantity(itemId, request, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Cart item updated successfully", response));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<ApiResponse<CartResponse>> removeItemFromCart(
            @PathVariable Long itemId,
            Authentication authentication) {
        String userEmail = authentication.getName();
        CartResponse response = cartService.removeItemFromCart(itemId, userEmail);
        return ResponseEntity.ok(ApiResponse.success("Cart item removed successfully", response));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(Authentication authentication) {
        String userEmail = authentication.getName();
        cartService.clearCart(userEmail);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared successfully", null));
    }
}
