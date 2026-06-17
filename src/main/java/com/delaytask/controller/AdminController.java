package com.delaytask.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.delaytask.entity.Order;
import com.delaytask.entity.OrderItem;
import com.delaytask.entity.Product;
import com.delaytask.mapper.OrderItemMapper;
import com.delaytask.mapper.OrderMapper;
import com.delaytask.mapper.ProductMapper;
import com.delaytask.util.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@Tag(name = "管理后台接口")
public class AdminController {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    private void checkAdmin(HttpServletRequest request) {
        String username = (String) request.getAttribute("username");
        if (!"admin".equals(username)) {
            throw new RuntimeException("无权访问管理后台");
        }
    }

    @GetMapping("/product/list")
    @Operation(summary = "商品管理列表（含已下架）")
    public Result<?> listProducts(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        checkAdmin(request);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        IPage<Product> productPage = productMapper.selectPage(new Page<>(page, size), wrapper);
        return Result.success(productPage);
    }

    @PostMapping("/product/{id}/toggle")
    @Operation(summary = "上架/下架商品")
    public Result<?> toggleProduct(HttpServletRequest request, @PathVariable Long id) {
        checkAdmin(request);
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        int newStatus = product.getStatus() == 1 ? 0 : 1;
        product.setStatus(newStatus);
        productMapper.updateById(product);
        return Result.success(newStatus == 1 ? "商品已上架" : "商品已下架");
    }

    @PostMapping("/product/update")
    @Operation(summary = "编辑商品信息（库存/价格/名称/分类）")
    public Result<?> updateProduct(HttpServletRequest request, @RequestBody Map<String, Object> params) {
        checkAdmin(request);
        Long id = Long.valueOf(params.get("id").toString());
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "商品不存在");
        }
        if (params.containsKey("name")) {
            product.setName((String) params.get("name"));
        }
        if (params.containsKey("category")) {
            product.setCategory((String) params.get("category"));
        }
        if (params.containsKey("price")) {
            product.setPrice(new BigDecimal(params.get("price").toString()));
        }
        if (params.containsKey("stock")) {
            product.setStock(Integer.valueOf(params.get("stock").toString()));
        }
        if (params.containsKey("description")) {
            product.setDescription((String) params.get("description"));
        }
        productMapper.updateById(product);
        return Result.success("商品信息已更新");
    }

    @GetMapping("/order/list")
    @Operation(summary = "全部订单列表")
    public Result<?> listOrders(
            HttpServletRequest request,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status) {
        checkAdmin(request);
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getDeleted, 0);
        if (StringUtils.hasText(orderNo)) {
            wrapper.like(Order::getOrderNo, orderNo);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Order::getStatus, status);
        }
        wrapper.orderByDesc(Order::getCreateTime);
        IPage<Order> orderPage = orderMapper.selectPage(new Page<>(page, size), wrapper);
        for (Order order : orderPage.getRecords()) {
            List<OrderItem> items = orderItemMapper.selectByOrderId(order.getId());
            order.setItems(items);
        }
        return Result.success(orderPage);
    }
}
