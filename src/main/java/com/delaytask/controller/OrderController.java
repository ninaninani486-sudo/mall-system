package com.delaytask.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delaytask.dto.OrderDTO;
import com.delaytask.entity.Order;
import com.delaytask.service.OrderService;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/order")
@Tag(name = "订单接口")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/create")
    @Operation(summary = "创建订单")
    public Result<?> createOrder(HttpServletRequest request, @RequestBody OrderDTO dto) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            Order order = orderService.createOrder(userId, dto);
            return Result.success(order);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @GetMapping("/detail/{orderNo}")
    @Operation(summary = "订单详情")
    public Result<?> getOrderDetail(HttpServletRequest request, @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute("userId");
        Order order = orderService.getOrderDetail(userId, orderNo);
        if (order == null) {
            return Result.error("订单不存在");
        }
        return Result.success(order);
    }

    @GetMapping("/list")
    @Operation(summary = "我的订单列表")
    public Result<?> getUserOrders(HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        List<Order> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }

    @GetMapping("/page")
    @Operation(summary = "我的订单列表（分页+搜索）")
    public Result<?> getUserOrdersPage(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        Long userId = (Long) request.getAttribute("userId");
        IPage<Order> orderPage = orderService.getUserOrdersPage(userId, new Page<>(page, size), keyword);
        return Result.success(orderPage);
    }

    @PostMapping("/cancel/{orderNo}")
    @Operation(summary = "取消订单")
    public Result<?> cancelOrder(HttpServletRequest request, @PathVariable String orderNo) {
        Long userId = (Long) request.getAttribute("userId");
        try {
            orderService.cancelOrder(userId, orderNo);
            return Result.success("取消成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
