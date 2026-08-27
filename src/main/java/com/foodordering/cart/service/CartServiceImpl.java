package com.foodordering.cart.service;

import com.foodordering.cart.dto.AddToCartRequest;
import com.foodordering.cart.dto.CartItemResponse;
import com.foodordering.cart.dto.CartResponse;
import com.foodordering.cart.dto.UpdateCartItemRequest;
import com.foodordering.cart.entity.Cart;
import com.foodordering.cart.entity.CartItem;
import com.foodordering.cart.repository.CartItemRepository;
import com.foodordering.cart.repository.CartRepository;
import com.foodordering.exception.BadRequestException;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.food.entity.Food;
import com.foodordering.food.service.FoodService;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service implementation managing customer cart lifecycle, auto-subtotal calculation, and orphan removal.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final FoodService foodService;

    @Override
    public CartResponse addItemToCart(AddToCartRequest request, String userEmail) {
        User user = userService.findEntityByEmail(userEmail);

        if (user.getRole() != Role.CUSTOMER) {
            throw new BadRequestException("Only Customers are allowed to add items to cart and place orders.");
        }

        Cart cart = getOrCreateCartEntity(userEmail);
        Food food = foodService.findEntityById(request.getFoodId());

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getFood().getId().equals(food.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + request.getQuantity());
            item.setPrice(item.getFood().getPrice() * item.getQuantity());
        } else {
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .food(food)
                    .quantity(request.getQuantity())
                    .price(food.getPrice() * request.getQuantity())
                    .build();
            cart.getItems().add(newItem);
        }

        recalculateTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse getCartByUserEmail(String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        return mapToResponse(cart);
    }

    @Override
    public CartResponse updateCartItemQuantity(Long itemId, UpdateCartItemRequest request, String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        CartItem cartItem = findCartItemById(itemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to the user's cart");
        }

        if (request.getQuantity() <= 0) {
            cart.getItems().remove(cartItem);
        } else {
            cartItem.setQuantity(request.getQuantity());
            cartItem.setPrice(cartItem.getFood().getPrice() * request.getQuantity());
        }

        recalculateTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    public CartResponse removeItemFromCart(Long itemId, String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        CartItem cartItem = findCartItemById(itemId);

        if (!cartItem.getCart().getId().equals(cart.getId())) {
            throw new UnauthorizedException("Cart item does not belong to the user's cart");
        }

        cart.getItems().remove(cartItem);
        recalculateTotalPrice(cart);
        Cart savedCart = cartRepository.save(cart);
        return mapToResponse(savedCart);
    }

    @Override
    public void clearCart(String userEmail) {
        Cart cart = getOrCreateCartEntity(userEmail);
        cart.getItems().clear();
        cart.setTotalPrice(0.0);
        cartRepository.save(cart);
    }

    @Override
    public Cart getOrCreateCartEntity(String userEmail) {
        User user = userService.findEntityByEmail(userEmail);
        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> {
                    Cart newCart = Cart.builder()
                            .user(user)
                            .totalPrice(0.0)
                            .items(new ArrayList<>())
                            .build();
                    return cartRepository.save(newCart);
                });
    }

    private CartItem findCartItemById(Long itemId) {
        return cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart item not found with id: " + itemId));
    }

    private void recalculateTotalPrice(Cart cart) {
        double total = cart.getItems().stream()
                .mapToDouble(CartItem::getPrice)
                .sum();
        cart.setTotalPrice(total);
    }

    private CartResponse mapToResponse(Cart cart) {
        return CartResponse.builder()
                .id(cart.getId())
                .userId(cart.getUser().getId())
                .totalPrice(cart.getTotalPrice())
                .items(cart.getItems().stream()
                        .map(item -> CartItemResponse.builder()
                                .id(item.getId())
                                .foodId(item.getFood().getId())
                                .foodName(item.getFood().getName())
                                .foodPrice(item.getFood().getPrice())
                                .quantity(item.getQuantity())
                                .price(item.getPrice())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
