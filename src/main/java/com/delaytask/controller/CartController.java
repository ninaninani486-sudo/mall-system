package com.delaytask.controller;

import com.delaytask.entity.Cart;
import com.delaytask.service.CartService;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@Tag(name = "购物车接口")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/list")
    @Operation(summary = "购物车列表")
    public Result<?> getCartList(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Cart> cartList = cartService.getCartList(userId);
        return Result.success(cartList);
    }

    @PostMapping("/add")
    @Operation(summary = "添加到购物车")
    public Result<?> addToCart(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        Cart cart = cartService.addToCart(userId, productId, quantity);
        return Result.success(cart);
    }

    @PutMapping("/update")
    @Operation(summary = "更新购物车数量")
    public Result<?> updateQuantity(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        Long userId = (Long) request.getAttribute("userId");
        Long productId = Long.valueOf(params.get("productId").toString());
        Integer quantity = Integer.valueOf(params.get("quantity").toString());
        Cart cart = cartService.updateQuantity(userId, productId, quantity);
        return Result.success(cart);
    }

    @DeleteMapping("/remove/{productId}")
    @Operation(summary = "从购物车移除")
    public Result<?> removeFromCart(HttpServletRequest request, @PathVariable Long productId) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.removeFromCart(userId, productId);
        return Result.success("移除成功");
    }

    @DeleteMapping("/clear")
    @Operation(summary = "清空购物车")
    public Result<?> clearCart(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        cartService.clearCart(userId);
        return Result.success("清空成功");
    }
}
