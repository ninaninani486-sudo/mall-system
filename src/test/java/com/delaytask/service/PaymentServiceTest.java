package com.delaytask.service;

import com.delaytask.entity.*;
import com.delaytask.mapper.*;
import com.delaytask.service.impl.PaymentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private UserAccountMapper userAccountMapper;

    @Mock
    private AccountTransactionMapper transactionMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private CartMapper cartMapper;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    private Order order;
    private UserAccount account;

    @BeforeEach
    void setUp() {
        order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setOrderNo("ORD001");
        order.setStatus("PENDING");
        order.setPayAmount(BigDecimal.valueOf(100));

        account = new UserAccount();
        account.setUserId(1L);
        account.setBalance(BigDecimal.valueOf(500));
        account.setVersion(0);
    }

    @Test
    void payOrder_balance_shouldSucceed() {
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);
        when(userAccountMapper.selectByUserId(1L)).thenReturn(account);
        when(userAccountMapper.updateBalance(1L, BigDecimal.valueOf(-100), 0)).thenReturn(1);

        Payment payment = paymentService.payOrder(1L, "ORD001", "BALANCE");

        assertNotNull(payment);
        assertEquals("SUCCESS", payment.getStatus());
        assertEquals("BALANCE", payment.getPayType());
        verify(orderMapper).updateById(argThat(o -> "PAID".equals(o.getStatus())));
    }

    @Test
    void payOrder_shouldThrow_whenOrderNotExist() {
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(null);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.payOrder(2L, "ORD001", "BALANCE"));
        assertTrue(ex.getMessage().contains("订单不存在"));
    }

    @Test
    void payOrder_shouldThrow_whenBalanceInsufficient() {
        account.setBalance(BigDecimal.valueOf(50));
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);
        when(userAccountMapper.selectByUserId(1L)).thenReturn(account);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.payOrder(1L, "ORD001", "BALANCE"));
        assertTrue(ex.getMessage().contains("余额不足"));
    }

    @Test
    void payOrder_shouldThrow_whenConcurrentUpdate() {
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);
        when(userAccountMapper.selectByUserId(1L)).thenReturn(account);
        when(userAccountMapper.updateBalance(1L, BigDecimal.valueOf(-100), 0)).thenReturn(0);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> paymentService.payOrder(1L, "ORD001", "BALANCE"));
        assertTrue(ex.getMessage().contains("支付失败"));
    }

    @Test
    void handleOrderTimeout_shouldCancelOrder() {
        OrderItem item = new OrderItem();
        item.setProductId(1L);
        item.setQuantity(2);
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);
        when(orderItemMapper.selectByOrderId(1L)).thenReturn(java.util.Arrays.asList(item));
        when(productMapper.restoreStock(1L, 2)).thenReturn(1);
        when(cartMapper.selectOne(any())).thenReturn(null);

        paymentService.handleOrderTimeout("ORD001");

        verify(orderMapper).updateById(argThat(o -> "CANCELLED".equals(o.getStatus())));
    }

    @Test
    void handleOrderTimeout_shouldSkip_whenAlreadyPaid() {
        order.setStatus("PAID");
        when(orderMapper.selectByOrderNo("ORD001")).thenReturn(order);

        paymentService.handleOrderTimeout("ORD001");

        verify(orderMapper, never()).updateById(any());
    }
}
