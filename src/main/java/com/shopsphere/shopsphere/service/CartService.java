package com.shopsphere.shopsphere.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.shopsphere.shopsphere.entity.Cart;
import com.shopsphere.shopsphere.entity.CartItem;
import com.shopsphere.shopsphere.entity.Product;
import com.shopsphere.shopsphere.entity.User;
import com.shopsphere.shopsphere.repository.CartItemRepository;
import com.shopsphere.shopsphere.repository.CartRepository;
import com.shopsphere.shopsphere.repository.ProductRepository;
import com.shopsphere.shopsphere.repository.UserRepository;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            UserRepository userRepository,
            ProductRepository productRepository) {

        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

   public Cart createCart(Long userId) {

    User user = userRepository.findById(userId).orElse(null);

    if (user == null) {
        return null;
    }

    Cart existingCart = cartRepository.findByUserId(userId).orElse(null);

    if (existingCart != null) {
        return existingCart;
    }

    Cart cart = new Cart();
    cart.setUser(user);

    return cartRepository.save(cart);
   }

    public Cart getCart(Long cartId) {

        return cartRepository.findById(cartId).orElse(null);
    }

    public CartItem addItemToCart(
        Long cartId,
        Long productId,
        Integer quantity) {

    Cart cart = cartRepository.findById(cartId).orElse(null);

    Product product = productRepository.findById(productId).orElse(null);

    if (cart == null || product == null) {
        return null;
    }

    // Quantity must be greater than 0
    if (quantity == null || quantity <= 0) {
        return null;
    }

    // Requested quantity cannot be greater than available stock
    if (quantity > product.getStock()) {
        return null;
    }

    CartItem cartItem = new CartItem();

    cartItem.setCart(cart);
    cartItem.setProduct(product);
    cartItem.setQuantity(quantity);

    return cartItemRepository.save(cartItem);
   }

    public List<CartItem> getCartItems(Long cartId) {

        return cartItemRepository.findAll()
                .stream()
                .filter(item ->
                        item.getCart().getId().equals(cartId))
                .toList();
    }

    public void removeCartItem(Long itemId) {

        cartItemRepository.deleteById(itemId);
    }
}