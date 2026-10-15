package com.shopsphere.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.shopsphere.shopsphere.entity.Cart;
import com.shopsphere.shopsphere.entity.CartItem;
import com.shopsphere.shopsphere.entity.Order;
import com.shopsphere.shopsphere.entity.OrderItem;
import com.shopsphere.shopsphere.entity.Product;
import com.shopsphere.shopsphere.entity.User;
import com.shopsphere.shopsphere.repository.CartItemRepository;
import com.shopsphere.shopsphere.repository.CartRepository;
import com.shopsphere.shopsphere.repository.OrderItemRepository;
import com.shopsphere.shopsphere.repository.OrderRepository;
import com.shopsphere.shopsphere.repository.ProductRepository;
import com.shopsphere.shopsphere.repository.UserRepository;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public OrderService(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public Order createOrder(Long userId) {

        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return null;
        }

        Cart cart = cartRepository.findAll()
                .stream()
                .filter(c -> c.getUser().getId().equals(userId))
                .findFirst()
                .orElse(null);

        if (cart == null) {
            return null;
        }

        List<CartItem> cartItems = cartItemRepository.findAll()
                .stream()
                .filter(item ->
                        item.getCart().getId().equals(cart.getId()))
                .toList();

        if (cartItems.isEmpty()) {
            return null;
        }

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            if (product.getStock() < cartItem.getQuantity()) {
                return null;
            }
        }

        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");

        Order savedOrder = orderRepository.save(order);

        for (CartItem cartItem : cartItems) {

            Product product = cartItem.getProduct();

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            orderItemRepository.save(orderItem);

            product.setStock(
                    product.getStock() - cartItem.getQuantity()
            );

            productRepository.save(product);

            cartItemRepository.deleteById(cartItem.getId());
        }

        return savedOrder;
    }

    public List<Order> getUserOrders(Long userId) {

        return orderRepository.findAll()
                .stream()
                .filter(order ->
                        order.getUser().getId().equals(userId))
                .toList();
    }

    public Order getOrderById(Long orderId) {

        return orderRepository.findById(orderId).orElse(null);
    }
}