package com.delaytask.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.dto.OrderDTO;
import com.delaytask.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, OrderDTO dto);

    Order getOrderDetail(Long userId, String orderNo);

    List<Order> getUserOrders(Long userId);

    void cancelOrder(Long userId, String orderNo);
}
