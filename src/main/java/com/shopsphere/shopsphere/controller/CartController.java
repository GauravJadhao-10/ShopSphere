package com.shopsphere.shopsphere.controller;

import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.shopsphere.shopsphere.entity.Cart;
import com.shopsphere.shopsphere.entity.CartItem;
import com.shopsphere.shopsphere.service.CartService;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/cart")


public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create/{userId}")
    public Cart createCart(@PathVariable Long userId) {
        return cartService.createCart(userId);
    }

    @GetMapping("/{cartId}")
    public Cart getCart(@PathVariable Long cartId) {
        return cartService.getCart(cartId);
    }

    @PostMapping("/{cartId}/items")
    public CartItem addItemToCart(
            @PathVariable Long cartId,
            @RequestParam Long productId,
            @RequestParam Integer quantity) {

        return cartService.addItemToCart(cartId, productId, quantity);
    }

    @GetMapping("/{cartId}/items")
    public List<CartItem> getCartItems(@PathVariable Long cartId) {
        return cartService.getCartItems(cartId);
    }

    @DeleteMapping("/items/{itemId}")
    public void removeCartItem(@PathVariable Long itemId) {
        cartService.removeCartItem(itemId);
    }
}