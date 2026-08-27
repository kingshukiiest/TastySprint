package com.foodordering.order.service;

import com.foodordering.address.dto.AddressResponse;
import com.foodordering.address.entity.Address;
import com.foodordering.address.service.AddressService;
import com.foodordering.cart.entity.Cart;
import com.foodordering.cart.service.CartService;
import com.foodordering.exception.BadRequestException;
import com.foodordering.exception.ResourceNotFoundException;
import com.foodordering.exception.UnauthorizedException;
import com.foodordering.order.dto.CreateOrderRequest;
import com.foodordering.order.dto.OrderItemResponse;
import com.foodordering.order.dto.OrderResponse;
import com.foodordering.order.dto.UpdateOrderStatusRequest;
import com.foodordering.order.entity.Order;
import com.foodordering.order.entity.OrderItem;
import com.foodordering.order.entity.OrderStatus;
import com.foodordering.order.repository.OrderRepository;
import com.foodordering.restaurant.entity.Restaurant;
import com.foodordering.restaurant.repository.RestaurantRepository;
import com.foodordering.user.entity.Role;
import com.foodordering.user.entity.User;
import com.foodordering.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation managing order placement, order status updates, and order history lookups.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final AddressService addressService;
    private final UserService userService;
    private final RestaurantRepository restaurantRepository;

    @Override
    public OrderResponse createOrder(CreateOrderRequest request, String customerEmail) {
        User customer = userService.findEntityByEmail(customerEmail);
        Cart cart = cartService.getOrCreateCartEntity(customerEmail);

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Cannot place order with an empty shopping cart");
        }

        Address deliveryAddress = addressService.findEntityById(request.getDeliveryAddressId());
        if (!deliveryAddress.getUser().getId().equals(customer.getId())) {
            throw new UnauthorizedException("Delivery address does not belong to the logged-in user");
        }

        Restaurant restaurant = cart.getItems().get(0).getFood().getRestaurant();

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .totalAmount(cart.getTotalPrice())
                .status(OrderStatus.PLACED)
                .deliveryAddress(deliveryAddress)
                .items(new ArrayList<>())
                .build();

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> OrderItem.builder()
                        .order(order)
                        .food(cartItem.getFood())
                        .quantity(cartItem.getQuantity())
                        .price(cartItem.getPrice())
                        .build())
                .collect(Collectors.toList());

        order.setItems(orderItems);
        Order savedOrder = orderRepository.save(order);

        cartService.clearCart(customerEmail);

        return mapToResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id, String userEmail) {
        Order order = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner = order.getRestaurant().getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCustomer && !isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to view this order");
        }

        return mapToResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = userService.findEntityByEmail(userEmail);

        if (user.getRole() == Role.ADMIN) {
            return orderRepository.findAll().stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        } else if (user.getRole() == Role.RESTAURANT_OWNER) {
            List<Restaurant> restaurants = restaurantRepository.findByOwnerId(user.getId());
            List<Order> ownerOrders = new ArrayList<>();
            for (Restaurant r : restaurants) {
                ownerOrders.addAll(orderRepository.findByRestaurantIdOrderByOrderDateDesc(r.getId()));
            }
            return ownerOrders.stream().map(this::mapToResponse).collect(Collectors.toList());
        } else {
            return orderRepository.findByCustomerIdOrderByOrderDateDesc(user.getId()).stream()
                    .map(this::mapToResponse)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public OrderResponse updateOrderStatus(Long id, UpdateOrderStatusRequest request, String userEmail) {
        Order order = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        boolean isOwner = order.getRestaurant().getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new UnauthorizedException("Only restaurant owners or admins can update order status");
        }

        order.setStatus(request.getStatus());
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    public OrderResponse cancelOrder(Long id, String userEmail) {
        Order order = findEntityById(id);
        User user = userService.findEntityByEmail(userEmail);

        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner = order.getRestaurant().getOwner().getId().equals(user.getId());
        boolean isAdmin = user.getRole() == Role.ADMIN;

        if (!isCustomer && !isOwner && !isAdmin) {
            throw new UnauthorizedException("You are not authorized to cancel this order");
        }

        if (order.getStatus() == OrderStatus.DELIVERED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Order cannot be cancelled in its current state: " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public Order findEntityById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    private OrderResponse mapToResponse(Order order) {
        Address addr = order.getDeliveryAddress();
        AddressResponse addressResponse = AddressResponse.builder()
                .id(addr.getId())
                .houseNumber(addr.getHouseNumber())
                .street(addr.getStreet())
                .city(addr.getCity())
                .state(addr.getState())
                .pincode(addr.getPincode())
                .type(addr.getType())
                .userId(addr.getUser().getId())
                .build();

        return OrderResponse.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .customerPhone(order.getCustomer().getPhone())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .orderDate(order.getOrderDate())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus())
                .deliveryAddress(addressResponse)
                .items(order.getItems().stream()
                        .map(item -> OrderItemResponse.builder()
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
