package com.delaytask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.delaytask.dto.OrderDTO;
import com.delaytask.entity.Order;

import java.util.List;

public interface OrderService extends IService<Order> {

    Order createOrder(Long userId, OrderDTO dto);

    Order getOrderDetail(Long userId, String orderNo);

    List<Order> getUserOrders(Long userId);

    IPage<Order> getUserOrdersPage(Long userId, Page<Order> page, String keyword);

    void cancelOrder(Long userId, String orderNo);
}
