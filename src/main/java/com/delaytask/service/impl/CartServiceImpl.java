package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.entity.Cart;
import com.delaytask.entity.Product;
import com.delaytask.mapper.CartMapper;
import com.delaytask.mapper.ProductMapper;
import com.delaytask.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart>
        implements CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Override
    public List<Cart> getCartList(Long userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        for (Cart cart : cartList) {
            Product product = productMapper.selectById(cart.getProductId());
            cart.setProduct(product);
        }
        return cartList;
    }

    @Override
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart cart = baseMapper.selectOne(wrapper);

        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + quantity);
            cart.setChecked(1);
            baseMapper.updateById(cart);
        } else {
            cart = new Cart();
            cart.setUserId(userId);
            cart.setProductId(productId);
            cart.setQuantity(quantity);
            cart.setChecked(1);
            baseMapper.insert(cart);
        }
        return cart;
    }

    @Override
    public Cart updateQuantity(Long userId, Long productId, Integer quantity) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        Cart cart = baseMapper.selectOne(wrapper);

        if (cart != null) {
            if (quantity <= 0) {
                baseMapper.deleteById(cart.getId());
                return null;
            }
            cart.setQuantity(quantity);
            baseMapper.updateById(cart);
        }
        return cart;
    }

    @Override
    public void removeFromCart(Long userId, Long productId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId)
                .eq(Cart::getProductId, productId);
        baseMapper.delete(wrapper);
    }

    @Override
    public void clearCart(Long userId) {
        LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Cart::getUserId, userId);
        baseMapper.delete(wrapper);
    }
}
