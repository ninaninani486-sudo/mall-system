package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.entity.Cart;

import java.util.List;

public interface CartService extends IService<Cart> {

    List<Cart> getCartList(Long userId);

    Cart addToCart(Long userId, Long productId, Integer quantity);

    Cart updateQuantity(Long userId, Long productId, Integer quantity);

    void removeFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
