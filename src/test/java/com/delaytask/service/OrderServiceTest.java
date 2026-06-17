package com.delaytask.service;

import com.delaytask.dto.OrderDTO;
import com.delaytask.dto.OrderItemDTO;
import com.delaytask.entity.*;
import com.delaytask.mapper.*;
import com.delaytask.mq.TaskProducer;
import com.delaytask.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartMapper cartMapper;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private TaskProducer taskProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Product product;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setAddress("测试地址");
        orderDTO.setItems(Arrays.asList(new OrderItemDTO()));

        OrderItemDTO itemDTO = orderDTO.getItems().get(0);
        itemDTO.setProductId(1L);
        itemDTO.setQuantity(2);

        product = new Product();
        product.setId(1L);
        product.setName("测试商品");
        product.setPrice(BigDecimal.valueOf(99.99));
        product.setStock(10);
        product.setStatus(1);
    }

    @Test
    void createOrder_shouldSucceed() {
        when(productMapper.selectById(1L)).thenReturn(product);
        when(productMapper.deductStock(1L, 2)).thenReturn(1);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);
        when(orderItemMapper.insert(any(OrderItem.class))).thenReturn(1);

        Order order = orderService.createOrder(1L, orderDTO);

        assertNotNull(order);
        assertEquals("PENDING", order.getStatus());
        assertEquals(BigDecimal.valueOf(199.98), order.getTotalAmount());
        verify(taskProducer).sendOrderTimeoutTask(anyString(), anyLong());
    }

    @Test
    void createOrder_shouldThrow_whenProductNotExist() {
        when(productMapper.selectById(1L)).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(1L, orderDTO));
        assertTrue(ex.getMessage().contains("商品不存在"));
    }

    @Test
    void createOrder_shouldThrow_whenStockInsufficient() {
        product.setStock(1);
        when(productMapper.selectById(1L)).thenReturn(product);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> orderService.createOrder(1L, orderDTO));
        assertTrue(ex.getMessage().contains("库存不足"));
    }

    @Test
    void getOrderDetail_shouldReturnNull_whenNotOwned() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(2L);
        order.setOrderNo("ORD001");
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);

        Order result = orderService.getOrderDetail(1L, "ORD001");
        assertNull(result);
    }
}
