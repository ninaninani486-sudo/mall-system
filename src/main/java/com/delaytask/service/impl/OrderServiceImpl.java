package com.delaytask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.delaytask.dto.OrderDTO;
import com.delaytask.dto.OrderItemDTO;
import com.delaytask.entity.*;
import com.delaytask.mapper.*;
import com.delaytask.mq.TaskProducer;
import com.delaytask.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private UserAccountMapper userAccountMapper;

    @Autowired
    private TaskProducer taskProducer;

    @Override
    @Transactional
    public Order createOrder(Long userId, OrderDTO dto) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setStatus("PENDING");
        order.setAddress(dto.getAddress());
        order.setRemark(dto.getRemark() != null ? dto.getRemark() : "");
        order.setDeleted(0);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            if (product == null || product.getStatus() != 1) {
                throw new RuntimeException("商品不存在或已下架: " + itemDTO.getProductId());
            }
            if (product.getStock() < itemDTO.getQuantity()) {
                throw new RuntimeException("库存不足: " + product.getName());
            }

            int rows = productMapper.deductStock(product.getId(), itemDTO.getQuantity());
            if (rows == 0) {
                throw new RuntimeException("库存扣减失败，可能已被其他订单占用");
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);
        }

        order.setTotalAmount(totalAmount);
        order.setPayAmount(totalAmount);
        orderMapper.insert(order);

        for (OrderItemDTO itemDTO : dto.getItems()) {
            Product product = productMapper.selectById(itemDTO.getProductId());
            OrderItem item = new OrderItem();
            item.setOrderId(order.getId());
            item.setProductId(product.getId());
            item.setProductName(product.getName());
            item.setProductImage(product.getImage());
            item.setPrice(product.getPrice());
            item.setQuantity(itemDTO.getQuantity());
            item.setTotalAmount(product.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity())));
            orderItemMapper.insert(item);
        }

        for (OrderItemDTO itemDTO : dto.getItems()) {
            LambdaQueryWrapper<Cart> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Cart::getUserId, userId)
                    .eq(Cart::getProductId, itemDTO.getProductId());
            cartMapper.delete(wrapper);
        }

        taskProducer.sendOrderTimeoutTask(order.getOrderNo(), 30 * 60 * 1000L);

        log.info("订单创建成功, orderNo={}, totalAmount={}", order.getOrderNo(), totalAmount);
        return order;
    }

    @Override
    public Order getOrderDetail(Long userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            return null;
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        order.setItems(items);
        return order;
    }

    @Override
    public List<Order> getUserOrders(Long userId) {
        List<Order> orders = orderMapper.selectByUserId(userId);
        for (Order order : orders) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            order.setItems(items);
        }
        return orders;
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(userId)) {
            throw new RuntimeException("订单不存在");
        }
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许取消");
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
        for (OrderItem item : items) {
            productMapper.restoreStock(item.getProductId(), item.getQuantity());

            LambdaQueryWrapper<Cart> cartWrapper = new LambdaQueryWrapper<>();
            cartWrapper.eq(Cart::getUserId, userId)
                    .eq(Cart::getProductId, item.getProductId());
            Cart existingCart = cartMapper.selectOne(cartWrapper);

            if (existingCart != null) {
                existingCart.setQuantity(existingCart.getQuantity() + item.getQuantity());
                existingCart.setChecked(1);
                cartMapper.updateById(existingCart);
            } else {
                Cart cart = new Cart();
                cart.setUserId(userId);
                cart.setProductId(item.getProductId());
                cart.setQuantity(item.getQuantity());
                cart.setChecked(1);
                cartMapper.insert(cart);
            }
        }

        order.setStatus("CANCELLED");
        orderMapper.updateById(order);

        log.info("订单已取消, 商品已返回购物车, orderNo={}", orderNo);
    }

    private String generateOrderNo() {
        return "ORD" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
